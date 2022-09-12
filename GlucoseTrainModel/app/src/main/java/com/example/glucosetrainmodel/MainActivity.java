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

    private Permission permission;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initListener();
        bluetooth = new Bluetooth(this,this);
        bluetooth.start();
        Log.d(TAG,"Home on crreate");
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
        Intent intent =new Intent(this,Result.class);
        Log.d(TAG, new Gson().toJson(list));
        ArrayList<Integer> mq3_ppmValues = new ArrayList<>();
        for (int i = 0 ; i < ppmlist.size();i++){
            mq3_ppmValues.add(Integer.valueOf((int) ppmlist.get(i).getY()));
        }

        Integer getMinPpm = Collections.min(mq3_ppmValues);
        data.setMq3_ppm(Float.valueOf(String.format("%.2f",ppmToMmol(mq3_ppmi))));
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
        btn_play = (CardView) findViewById(R.id.btn_play);
        img_play = (ImageView) findViewById(R.id.img_play);
        txt_mq3ppm = (TextView) findViewById(R.id.txt_mq3ppm);
        txt_bmp_pressure = (TextView) findViewById(R.id.txt_bmppressure);
        txt_bmp_temperature = (TextView) findViewById(R.id.txt_temperature);
        txt_dht_humidity = (TextView) findViewById(R.id.txt_humidity);
        txt_dht_celcius = (TextView) findViewById(R.id.txt_celcius);
        txt_dht_fahrenheit = (TextView) findViewById(R.id.txt_fahrenheit);
        txt_dht_heatindex = (TextView) findViewById(R.id.txt_heatindex);

        btn_menu = (ImageView) findViewById(R.id.btn_menu);
        relative = (RelativeLayout) findViewById(R.id.relative);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new SensorDataAdapter(this,list);
        recyclerView.setAdapter(adapter);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private ArrayList<Entry> ppmlist = new ArrayList<>();
    private ArrayList<Entry> mq3_ppm = new ArrayList<>();
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
        }

        txt_mq3ppm.setText(getMMol);
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

        ppmlist = removeData(ppmlist);
        mq3_ppm = removeData(mq3_ppm);
        bmp_pressure = removeData(bmp_pressure);
        bmp_temperature= removeData(bmp_temperature);
        dht_humidity = removeData(dht_humidity);
        dht_celcius = removeData(dht_celcius);
        dht_fahrenheit= removeData(dht_fahrenheit);
        dht_heatindex= removeData(dht_heatindex);

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
