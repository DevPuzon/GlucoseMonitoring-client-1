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
    private TextView txt_mq3ppm,txt_bmp_pressure,txt_bmp_temperature,txt_dht_humidity,txt_dht_celcius,txt_dht_fahrenheit,txt_dht_heatindex;
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
        Log.d(TAG, new Gson().toJson(list));
        ArrayList<Integer> mq3_ppmValues = new ArrayList<>();
        for (int i = 0 ; i < ppmlist.size();i++){
            mq3_ppmValues.add(Integer.valueOf((int) ppmlist.get(i).getY()));
        }

        Integer getMinPpm = Collections.min(mq3_ppmValues);

        Float defppm = Float.parseFloat(String.format("%.2f",ppmToMmol(getMinPpm)));
        Float highppm = Float.parseFloat(String.format("%.2f",ppmToMmol(mq3_ppmi)));

        data.setMq3_ppm(getPredictmmol(defppm,highppm));
        data.setBmp_pressure(bmp_pressuref);
        data.setBmp_temperature(bmp_temperaturef);
        data.setDht_humidity(dht_humidityf);
        data.setDht_celcius(dht_celciusf);
        data.setDht_fahrenheit(dht_fahrenheitf);
        data.setDht_heatindex(dht_heatindexf);


        intent.putExtra("getMinPpm",String.valueOf(getMinPpm));
        intent.putExtra("sensorData",new Gson().toJson(data));
        intent.putExtra("entries",new Gson().toJson(list));
        startActivity(intent);
    }

    private void onPlayDetect() {
        list.clear();
        mq3_ppm.clear();
        ppmlist.clear();
        bmp_pressure.clear();
        bmp_temperature.clear();
        dht_humidity.clear();
        dht_celcius.clear();
        dht_fahrenheit.clear();
        dht_heatindex.clear();
        mq3_ppmi = 0 ;
        bmp_pressuref = 0 ;
        dht_humidityf = 0 ;
        dht_celciusf = 0 ;
        dht_fahrenheitf = 0 ;
        dht_heatindexf = 0 ;
        adapter.notifyDataSetChanged();
    }

    private void init() {
        btn_play = (CardView) v.findViewById(R.id.btn_play);
        img_play = (ImageView) v.findViewById(R.id.img_play);
        txt_mq3ppm = (TextView) v.findViewById(R.id.txt_mq3ppm);
        txt_bmp_pressure = (TextView) v.findViewById(R.id.txt_bmppressure);
        txt_bmp_temperature = (TextView) v.findViewById(R.id.txt_temperature);
        txt_dht_humidity = (TextView) v.findViewById(R.id.txt_humidity);
        txt_dht_celcius = (TextView) v.findViewById(R.id.txt_celcius);
        txt_dht_fahrenheit = (TextView) v.findViewById(R.id.txt_fahrenheit);
        txt_dht_heatindex = (TextView) v.findViewById(R.id.txt_heatindex);

        btn_menu = (ImageView) v.findViewById(R.id.btn_menu);
        relative = (RelativeLayout) v.findViewById(R.id.relative);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        adapter = new SensorDataAdapter(getContext(),list);
        recyclerView.setAdapter(adapter);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private ArrayList<Entry> mq3_ppm = new ArrayList<>();
    private ArrayList<Entry> ppmlist = new ArrayList<>();
    private ArrayList<Entry> bmp_pressure = new ArrayList<>();
    private ArrayList<Entry> bmp_temperature = new ArrayList<>();
    private ArrayList<Entry> dht_humidity = new ArrayList<>();
    private ArrayList<Entry> dht_celcius = new ArrayList<>();
    private ArrayList<Entry> dht_fahrenheit = new ArrayList<>();
    private ArrayList<Entry> dht_heatindex = new ArrayList<>();

    private int mq3_ppmi;
    private float bmp_pressuref;
    private float bmp_temperaturef;
    private float dht_humidityf;
    private float dht_celciusf;
    private float dht_fahrenheitf;
    private float dht_heatindexf;
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
        this.data = data;
        list.clear();
        Log.d(TAG, new Gson().toJson(data));

        String getMMol = String.format("%.2f",ppmToMmol(data.getMq3_ppm()));
        if (!isplay){
            Log.d(TAG,"Isplaying");
            mq3_ppmi = getHighInt(mq3_ppmi, (int) data.getMq3_ppm());
            bmp_pressuref = getHighFloat(bmp_pressuref,data.getBmp_pressure());
            bmp_temperaturef = getHighFloat(bmp_temperaturef,data.getBmp_temperature());
            dht_humidityf = getHighFloat(dht_humidityf,data.getDht_humidity());
            dht_celciusf = getHighFloat(dht_celciusf,data.getDht_celcius());
            dht_fahrenheitf = getHighFloat(dht_fahrenheitf,data.getDht_fahrenheit());
            dht_heatindexf = getHighFloat(dht_heatindexf,data.getDht_heatindex());
        }else{
            ppmlist = removeData(ppmlist);
            mq3_ppm = removeData(mq3_ppm);
            bmp_pressure = removeData(bmp_pressure);
            bmp_temperature= removeData(bmp_temperature);
            dht_humidity = removeData(dht_humidity);
            dht_celcius = removeData(dht_celcius);
            dht_fahrenheit= removeData(dht_fahrenheit);
            dht_heatindex= removeData(dht_heatindex);
        }
        txt_mq3ppm.setText(String.valueOf(data.getMq3_ppm()));

        txt_bmp_pressure.setText(Html.fromHtml("<b>Pressure </b>"+String.valueOf(data.getBmp_pressure())+" hPa"));
        txt_bmp_temperature.setText(Html.fromHtml("<b>Temperature </b>"+String.valueOf(data.getBmp_temperature())+" °C"));
        txt_dht_humidity.setText(Html.fromHtml("<b>Humidity </b>"+String.valueOf(data.getDht_humidity())));
        txt_dht_celcius.setText(Html.fromHtml("<b>Celcius </b>"+String.valueOf(data.getDht_celcius())+" °C"));
        txt_dht_fahrenheit.setText(Html.fromHtml("<b>Fahrenheit </b>"+String.valueOf(data.getDht_fahrenheit())+" °F"));
        txt_dht_heatindex.setText(Html.fromHtml("<b>Heat index </b>"+String.valueOf(data.getDht_heatindex())));

        ppmlist.add(new Entry(count,data.getMq3_ppm()));
        mq3_ppm.add(new Entry(count,Float.valueOf(getMMol)));
        bmp_pressure.add(new Entry(count,data.getBmp_pressure()));
        bmp_temperature.add(new Entry(count,data.getBmp_temperature()));
        dht_humidity.add(new Entry(count,data.getDht_humidity()));
        dht_celcius.add(new Entry(count,data.getDht_celcius()));
        dht_fahrenheit.add(new Entry(count,data.getDht_fahrenheit()));
        dht_heatindex.add(new Entry(count,data.getDht_heatindex()));


        list.add(new EntrySensorData("ppm",ppmlist));
        list.add(new EntrySensorData("Ketones mmol",mq3_ppm));
        list.add(new EntrySensorData("Pressure",bmp_pressure));
        list.add(new EntrySensorData("Bmp Temperature",bmp_temperature));
        list.add(new EntrySensorData("Humidity",dht_humidity));
        list.add(new EntrySensorData("Heat index",dht_heatindex));
        list.add(new EntrySensorData("Celcius",dht_celcius));
        list.add(new EntrySensorData("Fahrenheit",dht_fahrenheit));

        adapter.notifyDataSetChanged();
        count++;
    }

    double ppmToMmol(float PPM){
        double ppmInmmol = (double) ((PPM / 1000f) / 110.15f);
        Log.d(TAG+" ppm", String.valueOf(PPM));
        Log.d(TAG+" ppm1", String.valueOf(PPM/1000f));
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
