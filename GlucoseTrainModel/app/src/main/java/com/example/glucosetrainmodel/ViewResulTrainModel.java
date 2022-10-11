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
    private TextView txt_bgl, txt_calib;
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
        txt_bgl.setText(String.valueOf(sensorData.getBgl()));
        txt_calib.setText(Html.fromHtml("<b>Default volt calibrated </b>"+String.valueOf(sensorData.getVolt())+" volt"));

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
        txt_calib = (TextView) findViewById(R.id.txt_calib);
        txt_bgl = (TextView) findViewById(R.id.txt_bgl);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        adapter = new SensorDataAdapter(ViewResulTrainModel.this,entrySensorDatas);
        recyclerView.setAdapter(adapter);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ViewResulTrainModel.this));
    } 
}
