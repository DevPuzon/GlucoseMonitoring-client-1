package com.example.glucosetrainmodel;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.example.glucosetrainmodel.Adapter.SensorDataAdapter;
import com.example.glucosetrainmodel.Interactive.Loading;
import com.example.glucosetrainmodel.Pojo.EntrySensorData;
import com.example.glucosetrainmodel.Pojo.SensorData;
import com.example.glucosetrainmodel.Pojo.TrainPojo;
import com.example.glucosetrainmodel.Services.Bluetooth;
import com.github.mikephil.charting.data.Entry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.annotation.LayoutRes;
import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.michaelbel.bottomsheet.BottomSheet;
import org.michaelbel.bottomsheet.BottomSheetCallback;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements Bluetooth.IBluetooth {

    private final String TAG = "MainActivity";
    private Bluetooth bluetooth;

    private Loading loading;

    private RelativeLayout relative;
    private RecyclerView recyclerView;
    private SensorDataAdapter adapter;
    private ArrayList<EntrySensorData> list = new ArrayList<>();

    private ImageView btn_menu;
    private CardView btn_play;
    private ImageView img_play;
    private TextView txt_bgl,txt_calib;

    private Permission permission;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initListener();
        bluetooth = new Bluetooth(this,this);
        bluetooth.start();
        Log.d(TAG,"Home on onCreate");
        loading = new Loading(this);
        initPermision();
    }

    private void initPermision() {
        permission = new Permission(this,this);
        if (!permission.checkReadExternal()){
            permission.reqReadExternal();
            btn_play.setEnabled(false);
        }
    }

    private boolean isplay = true;
    private void initListener() {
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence[] items;
                Drawable[] drawables = new Drawable[0];

                BottomSheet.Builder builder = new BottomSheet.Builder(MainActivity.this);
                builder.setTitleMultiline(false)
                        .setItems(R.array.status_array, drawables, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onClickMenuBottom(which);
                            }
                        }).show();
            }
        });
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isplay){
                    //Playing
//                    img_play.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop));
//                    btn_play.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    btn_play.setVisibility(View.GONE);
                    onPlayDetect();
                }
//                else{
//                    //Stopped
//                    img_play.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
//                    btn_play.setCardBackgroundColor(Color.WHITE);
//                    onStopDetect();
//                }
            }
        });
    }

    private void onClickMenuBottom(int which) {
        String shortcuts[] = getResources().getStringArray(R.array.status_array);
        Log.d(TAG +"TAG ",shortcuts[which] );
        String statusclick = shortcuts[which];
        ArrayList<TrainPojo> trainPojos = new ArrayList<>();
        String stringdata = TrainModel.getModel();
        if (!TextUtils.isEmpty(stringdata)){
            trainPojos = new Gson().fromJson(stringdata,new TypeToken<ArrayList<TrainPojo>>(){}.getType());
            Log.d(TAG,new Gson().toJson(stringdata));
            ArrayList<TrainPojo> returnList = new ArrayList<>();
            for (int i = 0 ; i < trainPojos.size();i++){
                TrainPojo trainPojo  = trainPojos.get(i);
                if (trainPojo.getStatus().equals(statusclick)){
                    returnList.add(trainPojo);
                }
            }
            if (returnList.size() ==0){

                Toast.makeText(this,"No data yet",Toast.LENGTH_LONG).show();
                return;
            }
            Intent intent = new Intent(MainActivity.this,ResultTrainData.class)
                    .putExtra("list",new Gson().toJson(returnList))
                    .putExtra("title", statusclick);
            startActivity(intent);
        }else{
            Toast.makeText(this,"No data yet",Toast.LENGTH_LONG).show();
        }
    }
    double ppmToMmol(float PPM){
        double ppmInmmol = (double) ((PPM / 1000f) / 110.15f);
        Log.d(TAG+" ppm", String.valueOf(PPM));
        Log.d(TAG+" ppm1", String.valueOf(PPM/1000f));
        ppmInmmol = ppmInmmol * 1000;
        return ppmInmmol;
    }

    private void onStopDetect() {
        btn_play.setVisibility(View.VISIBLE);
        Intent intent =new Intent(this,Result.class);
        float addBgl = 0;
        for (int i = 0 ; i < bglList.size();i++){
            addBgl +=bglList.get(i).getY();
        }
        float addVolt = 0;
        for (int i = 0 ; i < voltList.size();i++){
            addVolt +=voltList.get(i).getY();
        }

        bgl = addBgl/bglList.size();
        volt = addVolt/voltList.size();
        bgl = Float.parseFloat(String.format("%.2f",bgl));
        volt = Float.parseFloat(String.format("%.2f",volt));

        data.setBgl(bgl);
        data.setVolt(volt);
        Log.d(TAG, "new Gson().toJson(list)"+new Gson().toJson(list));
        intent.putExtra("sensorData",new Gson().toJson(data));
        intent.putExtra("entries",new Gson().toJson(list));
        startActivity(intent);
        iCountGather = 0 ;
        isplay =!isplay;
    }

    private void onPlayDetect() {
        list.clear();
        bglList.clear();
        voltList.clear();
        bgl = 0 ;
        volt = 0 ;
        iCountGather = 0 ;
        isplay =!isplay;
        adapter.notifyDataSetChanged();
    }

    private void init() {
        btn_play = (CardView) findViewById(R.id.btn_play);
        img_play = (ImageView) findViewById(R.id.img_play);
        txt_bgl = (TextView) findViewById(R.id.txt_bgl);
        txt_calib = (TextView) findViewById(R.id.txt_calib);

        btn_menu = (ImageView) findViewById(R.id.btn_menu);
        relative = (RelativeLayout) findViewById(R.id.relative);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new SensorDataAdapter(this,list);
        recyclerView.setAdapter(adapter);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    private ArrayList<Entry> bglList = new ArrayList<>();
    private ArrayList<Entry> voltList = new ArrayList<>();

    private float bgl;
    private float volt;
    private int count = 0;
    private int iCountGather = 0;
    private int countGather = 30;


    private ArrayList<Entry> removeData(ArrayList<Entry> entries){
        ArrayList<Entry> entries1 = entries;
        if (entries1.size() > 15){
            entries1.remove(0);
        }
        return entries1;
    }


    private SensorData data;
    @Override
    public void getData(SensorData data) {
        loading.setMessage("Please wait...");
        loading.loadDialog.dismiss();
        Log.d(TAG,"SensorData"+ new Gson().toJson(data));
        this.data = data;

        if (!isplay){
            //Playing
            Log.d(TAG,"count >= countGather"+String.valueOf(count >= countGather));
            if(iCountGather >= countGather){
                onStopDetect();
            }
            bgl = getHighFloat(bgl,data.getBgl());
            volt = getHighFloat(volt,data.getVolt());
        }else{
            bglList = removeData(bglList);
            voltList = removeData(voltList);
        }
        txt_bgl.setText(String.valueOf(data.getBgl()));
        txt_calib.setText(Html.fromHtml("<b>Default volt calibrated </b>"+String.valueOf(data.getVolt())+" volt"));


        bglList.add(new Entry(count,data.getBgl()));
        voltList.add(new Entry(count,data.getVolt()));

        list.clear();
        list.add(new EntrySensorData("mg/dl",bglList));
        list.add(new EntrySensorData("volt",voltList));

        adapter.notifyDataSetChanged();
        count++;
        iCountGather ++;
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

}
