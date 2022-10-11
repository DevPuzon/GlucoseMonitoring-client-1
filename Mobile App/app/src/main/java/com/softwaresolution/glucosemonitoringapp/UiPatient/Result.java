package com.softwaresolution.glucosemonitoringapp.UiPatient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwaresolution.glucosemonitoringapp.Adapter.SensorDataAdapter;
import com.softwaresolution.glucosemonitoringapp.Interactive.Loading;
import com.softwaresolution.glucosemonitoringapp.Pojo.EntrySensorData;
import com.softwaresolution.glucosemonitoringapp.Pojo.ResultPojo;
import com.softwaresolution.glucosemonitoringapp.Pojo.SensorData;
import com.softwaresolution.glucosemonitoringapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Result extends AppCompatActivity {

    private String TAG="ResultPojo";
    private TextView txt_bgl, txt_calib,txt_status;

    private SensorData sensorData;
    private String status = "normal";
    private ArrayList<EntrySensorData> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private SensorDataAdapter adapter;
    private Button btn_save;
    private Loading loading;
    public boolean isViewHisory = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_result);

        findViewById(R.id.v_normal).setVisibility(View.GONE);
        findViewById(R.id.v_pre).setVisibility(View.GONE);
        findViewById(R.id.v_diabetes).setVisibility(View.GONE);

        loading = new Loading(Result.this);
        Intent intent = getIntent();
        String passSensordata =intent.getStringExtra("sensorData");
        String passEntries =intent.getStringExtra("entries");
        isViewHisory =intent.getBooleanExtra("isViewHisory",true);
        sensorData = new Gson().fromJson(passSensordata,SensorData.class);
        list = new Gson().fromJson(passEntries, new TypeToken<ArrayList<EntrySensorData>>(){}.getType());
        Log.d(TAG, passSensordata);
        Log.d(TAG, passEntries);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        init();
        initProperties();
        initlistener();
    }

    private void initProperties() {
        txt_bgl.setText(String.valueOf(sensorData.getBgl()));
        txt_calib.setText(Html.fromHtml("<b>Default volt calibrated </b>"+String.valueOf(sensorData.getBgl())+" volt"));

        status = getStatus(sensorData.getBgl());
        txt_status.setText( status.substring(0, 1).toUpperCase() + status.substring(1));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void init() {
        btn_save = (Button) findViewById(R.id.btn_save);
        if (!isViewHisory){
            btn_save.setVisibility(View.GONE);
        }
        txt_status = (TextView) findViewById(R.id.txt_status);
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
                if (!isSave){
                    //Save data
                    saveData();
                }else {
                    //Already save
                    Toast.makeText(Result.this,"Already saved the data",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private String getStatus(float bgl){
        Log.d(TAG, "getStatus"+String.valueOf(bgl));
        String ret = "";
        if (bgl > 125){
            ret = "diabetic";
            findViewById(R.id.v_normal).setVisibility(View.GONE);
            findViewById(R.id.v_pre).setVisibility(View.GONE);
            findViewById(R.id.v_diabetes).setVisibility(View.VISIBLE);
        }else if(bgl > 100 && bgl < 125){
            ret = "pre-diabetic";
            findViewById(R.id.v_normal).setVisibility(View.GONE);
            findViewById(R.id.v_pre).setVisibility(View.VISIBLE);
            findViewById(R.id.v_diabetes).setVisibility(View.GONE);
        }else{
            ret = "normal";
            findViewById(R.id.v_normal).setVisibility(View.VISIBLE);
            findViewById(R.id.v_pre).setVisibility(View.GONE);
            findViewById(R.id.v_diabetes).setVisibility(View.GONE);
        }
        return ret;
    }


    Date c = Calendar.getInstance().getTime();
    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy  HH:mm:ss");
    String formattedDate = df.format(c);
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private boolean isSave = false;

    //Api
    private void saveData(){
        ResultPojo result = new ResultPojo(sensorData,list,formattedDate,String.valueOf(sensorData.getBgl()),status);
        loading.loadDialog.show();
        firestore.collection("Glucose Result Client 1")
                .document(formattedDate).set(result)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loading.loadDialog.dismiss();
                        if (task.isSuccessful()) {
                            isSave = true;
                            Toast.makeText(Result.this,"Saved",Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(Result.this,"Failed to save",Toast.LENGTH_LONG).show();
                        }
                        onBackPressed();
                    }
                });
    }
}
