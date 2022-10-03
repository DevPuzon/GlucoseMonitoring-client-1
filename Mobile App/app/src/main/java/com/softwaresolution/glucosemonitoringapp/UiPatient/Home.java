package com.softwaresolution.glucosemonitoringapp.UiPatient;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;
import com.google.gson.Gson;
import com.softwaresolution.glucosemonitoringapp.Adapter.SensorDataAdapter;
import com.softwaresolution.glucosemonitoringapp.Interactive.Loading;
import com.softwaresolution.glucosemonitoringapp.Pojo.EntrySensorData;
import com.softwaresolution.glucosemonitoringapp.Pojo.SensorData;
import com.softwaresolution.glucosemonitoringapp.R;
import com.softwaresolution.glucosemonitoringapp.Services.Bluetooth;

import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Home extends Fragment implements Bluetooth.IBluetooth {

    private final String TAG = "Home";
    private Bluetooth bluetooth;

    private Loading loading;

    private RelativeLayout relative;
    private RecyclerView recyclerView;
    private SensorDataAdapter adapter;
    private ArrayList<EntrySensorData> list = new ArrayList<>();

    private ImageView btn_menu;
    private TextView txt_bgl,txt_calib;
    private CardView btn_play;
    private ImageView img_play;
    private View v;
    private IHome iHome;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.ui_home, container, false);

        init();
        initListener();
        bluetooth = new Bluetooth(getContext(),Home.this);
        bluetooth.start();
        Log.d(TAG,"Home on crreate");
        loading = new Loading(getContext());
//        loading.loadDialog.show();
        return v;
    }

    private boolean isplay = true;
    private void initListener() {
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iHome.onClickMenu();
            }
        });
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isplay){
                    //Playing
                    img_play.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop));
                    btn_play.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    onPlayDetect();
                }else{
                    //Stopped
                    img_play.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
                    btn_play.setCardBackgroundColor(Color.WHITE);
                    onStopDetect();
                }
                isplay = !isplay;
            }
        });
    }

    private void onStopDetect() {
        Intent intent =new Intent(getContext(),Result.class);
        ArrayList<Integer> bglValues = new ArrayList<>();
        for (int i = 0 ; i < bglList.size();i++){
            bglValues.add(Integer.valueOf((int) bglList.get(i).getY()));
        }
        float bglAve = bgl;
        float voltAve = volt;

        data.setBgl(bgl);
        data.setVolt(volt);
        intent.putExtra("bglAve",String.valueOf(bglAve));
        intent.putExtra("voltAve",String.valueOf(voltAve));
        intent.putExtra("sensorData",new Gson().toJson(data));
        intent.putExtra("entries",new Gson().toJson(list));
        startActivity(intent);
    }

    private void onPlayDetect() {
        list.clear();
        bglList.clear();
        voltList.clear();
        bgl = 0 ;
        volt = 0 ;
        adapter.notifyDataSetChanged();
    }

    private void init() {
        btn_play = (CardView) v.findViewById(R.id.btn_play);
        img_play = (ImageView) v.findViewById(R.id.img_play);
        txt_bgl = (TextView) v.findViewById(R.id.txt_bgl);
        txt_calib = (TextView) v.findViewById(R.id.txt_calib);

        btn_menu = (ImageView) v.findViewById(R.id.btn_menu);
        relative = (RelativeLayout) v.findViewById(R.id.relative);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        adapter = new SensorDataAdapter(getContext(),list);
        recyclerView.setAdapter(adapter);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private ArrayList<Entry> bglList = new ArrayList<>();
    private ArrayList<Entry> voltList = new ArrayList<>();

    private float bgl;
    private float volt;
    private int count = 0;


    private ArrayList<Entry> removeData(ArrayList<Entry> entries){
        ArrayList<Entry> entries1 = entries;
        if (entries1.size() > 15){
            entries1.remove(0);
        }
        return entries1;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        iHome = (IHome) context;
    }

    private SensorData data;
    @Override
    public void getData(SensorData data) {
        loading.setMessage("Please wait...");
        loading.loadDialog.dismiss();
        Log.d(TAG,"SensorData"+ new Gson().toJson(data));
        this.data = data;
        list.clear();

        if (!isplay){
            bgl = getHighFloat(bgl,data.getBgl());
            volt = getHighFloat(volt,data.getVolt());
        }else{
            bglList = removeData(bglList);
            voltList = removeData(voltList);
        }
        txt_bgl.setText(String.valueOf(data.getBgl()));
        txt_calib.setText(Html.fromHtml("<b>Default calibrated </b>"+String.valueOf(data.getVolt())+" volt"));


        bglList.add(new Entry(count,data.getBgl()));
        voltList.add(new Entry(count,data.getVolt()));

        list.add(new EntrySensorData("mg/dl",bglList));
        list.add(new EntrySensorData("volt",voltList));

        adapter.notifyDataSetChanged();
        count++;
    }

    double ppmToMmol(float PPM){
        double ppmInmmol = (double) ((PPM / 1000f) / 110.15f);
        ppmInmmol = ppmInmmol * 1000;
        return ppmInmmol;
    }

    private Float getHighFloat(float def,float now){
        float ret = 0 ;
        if (def < now ){
            ret = now;
        }else {
            ret = def;
        }
        return ret;
    }

    private Integer getHighInt(int def,int now){
        int ret = 0 ;
        if (def < now ){
            ret = now;
        }else {
            ret = def;
        }
        return ret;
    }

    @Override
    public void calibrating() {
        loading.setMessage("Calibrating....");
        loading.loadDialog.show();
    }

    public interface IHome{
        void onClickMenu();
    }

    private float getPredictmmol(float defppm,float highppm){
        float ret=0;


        Log.d(TAG, "defppm"+String.valueOf(defppm));
        Log.d(TAG, "highppm"+String.valueOf(highppm));
        ret =  highppm - defppm;
        return Float.parseFloat(String.format("%.2f",ret)) ;
    }
}
