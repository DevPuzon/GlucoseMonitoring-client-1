package com.example.glucosetrainmodel;

import android.content.Intent;
import android.nfc.Tag;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glucosetrainmodel.Pojo.TrainPojo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.example.glucosetrainmodel.Adapter.SensorDataAdapter;
import com.example.glucosetrainmodel.Interactive.Loading;
import com.example.glucosetrainmodel.Pojo.EntrySensorData;
import com.example.glucosetrainmodel.Pojo.ResultPojo;
import com.example.glucosetrainmodel.Pojo.SensorData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Result extends AppCompatActivity {

    private String TAG="ResultPojo";

    private SensorData sensorData;

    private ArrayList<EntrySensorData> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private SensorDataAdapter adapter;
    private Button btn_save;
    private String getMinPpm;
    private Loading loading;
    private  TextView txt_name;
    private Spinner spin_status;
    private ArrayList<TrainPojo> trainPojos = new ArrayList<>();
    private TextView txt_bgl, txt_calib;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_result);
        loading = new Loading(Result.this);
        Intent intent = getIntent();
        getMinPpm =intent.getStringExtra("getMinPpm");
        String passSensordata =intent.getStringExtra("sensorData");
        String passEntries =intent.getStringExtra("entries");
        sensorData = new Gson().fromJson(passSensordata,SensorData.class);
        list = new Gson().fromJson(passEntries, new TypeToken<ArrayList<EntrySensorData>>(){}.getType());
        Log.d(TAG, passSensordata);
        Log.d(TAG, "passEntries"+passEntries);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        init();
        initProperties();
        initlistener();

        initDatas();
    }

    private void initDatas() {
        String stringdata = TrainModel.getModel();
        if (!TextUtils.isEmpty(stringdata)){
            trainPojos = new Gson().fromJson(stringdata,new TypeToken<ArrayList<TrainPojo>>(){}.getType());
            Log.d(TAG,new Gson().toJson(stringdata));
        }
    }

    private void initProperties() {
        spin_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                status = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
        btn_save = (Button) findViewById(R.id.btn_save);
        txt_calib = (TextView) findViewById(R.id.txt_calib);
        txt_bgl = (TextView) findViewById(R.id.txt_bgl);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        adapter = new SensorDataAdapter(Result.this,list);
        recyclerView.setAdapter(adapter);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(Result.this));
    }

    private void initlistener() {
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(txt_name.getText().toString()) ||
                TextUtils.isEmpty(status)){
                    Toast.makeText(Result.this,"name is invalid",Toast.LENGTH_LONG).show();
                    return;
                }
                saveData();
                Toast.makeText(Result.this,"Saved",Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }


    private String status;
    Date c = Calendar.getInstance().getTime();
    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy  HH:mm:ss");
    String formattedDate = df.format(c);
    private void saveData(){
        trainPojos.add(new TrainPojo(txt_name.getText().toString(),
                status,formattedDate,sensorData,list,getMinPpm));
        TrainModel.saveData(new Gson().toJson(trainPojos));
    }
}
