package com.example.glucosetrainmodel;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glucosetrainmodel.Adapter.SensorDataAdapter;
import com.example.glucosetrainmodel.Interactive.Loading;
import com.example.glucosetrainmodel.Pojo.EntrySensorData;
import com.example.glucosetrainmodel.Pojo.SensorData;
import com.example.glucosetrainmodel.Pojo.TrainPojo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ViewResulTrainModel  extends AppCompatActivity {

    private String TAG="ResultPojo";
    private TextView txt_mq3ppm,txt_bmp_pressure,txt_bmp_temperature,txt_dht_humidity,
            txt_dht_celcius,txt_dht_fahrenheit,txt_dht_heatindex,txt_calib;

    private SensorData sensorData;

    private ArrayList<EntrySensorData> entrySensorDatas = new ArrayList<>();
    private RecyclerView recyclerView;
    private SensorDataAdapter adapter;
    private Button btn_save;
    private String getMinPpm;
    private Loading loading;
    private  TextView txt_name;
    private Spinner spin_status;
    private TrainPojo trainPojo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_result);
        setTitle(getIntent().getStringExtra("title"));

        loading = new Loading(ViewResulTrainModel.this);
        Intent intent = getIntent();
        String stringTrain =intent.getStringExtra("trainPojo");
        trainPojo = new Gson().fromJson(stringTrain,TrainPojo.class);
        Log.d(TAG,stringTrain);
        getMinPpm = trainPojo.getMinPPm();

        sensorData = trainPojo.getSensorData();
        entrySensorDatas = trainPojo.getEntrilList();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        init();
        initProperties();

    }


    private void initProperties() {
        spin_status.setVisibility(View.GONE);
        txt_mq3ppm.setText(String.valueOf(sensorData.getMq3_ppm()));
        txt_calib.setText(Html.fromHtml("<b>Default calibrated </b>"+String.valueOf(getMinPpm)));
        txt_bmp_pressure.setText(Html.fromHtml("<b>Pressure </b>"+String.valueOf(sensorData.getBmp_pressure())+" hPa"));
        txt_bmp_temperature.setText(Html.fromHtml("<b>Temperature </b>"+String.valueOf(sensorData.getBmp_temperature())+" °C"));
        txt_dht_humidity.setText(Html.fromHtml("<b>Humidity </b>"+String.valueOf(sensorData.getDht_humidity())));
        txt_dht_celcius.setText(Html.fromHtml("<b>Celcius </b>"+String.valueOf(sensorData.getDht_celcius())+" °C"));
        txt_dht_fahrenheit.setText(Html.fromHtml("<b>Fahrenheit </b>"+String.valueOf(sensorData.getDht_fahrenheit())+" °F"));
        txt_dht_heatindex.setText(Html.fromHtml("<b>Heat index </b>"+String.valueOf(sensorData.getDht_heatindex())));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void init() {
        spin_status= (Spinner) findViewById(R.id.spin_satus);
        txt_name = (TextView) findViewById(R.id.txt_name);
        txt_name.setEnabled(false);
        txt_name.setText(trainPojo.getName());
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_save.setEnabled(false);
        txt_calib = (TextView) findViewById(R.id.txt_calib);
        txt_mq3ppm = (TextView) findViewById(R.id.txt_mq3ppm);
        txt_bmp_pressure = (TextView) findViewById(R.id.txt_bmppressure);
        txt_bmp_temperature = (TextView) findViewById(R.id.txt_temperature);
        txt_dht_humidity = (TextView) findViewById(R.id.txt_humidity);
        txt_dht_celcius = (TextView) findViewById(R.id.txt_celcius);
        txt_dht_fahrenheit = (TextView) findViewById(R.id.txt_fahrenheit);
        txt_dht_heatindex = (TextView) findViewById(R.id.txt_heatindex);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        adapter = new SensorDataAdapter(ViewResulTrainModel.this,entrySensorDatas);
        recyclerView.setAdapter(adapter);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ViewResulTrainModel.this));
    } 
}
