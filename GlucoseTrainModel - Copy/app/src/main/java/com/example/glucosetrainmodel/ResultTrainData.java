package com.example.glucosetrainmodel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Filter;
import android.widget.Toast;

import com.example.glucosetrainmodel.Adapter.ResultTrainAdapter;
import com.example.glucosetrainmodel.Interactive.Loading;
import com.example.glucosetrainmodel.Pojo.TrainPojo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class ResultTrainData extends AppCompatActivity {
    private String TAG = "ResultTrainData";
    private RecyclerView recyclerView;
    private ResultTrainAdapter adapter;
    private ArrayList<TrainPojo> list = new ArrayList<>();

    private Loading loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_train_data);
        loading = new Loading(ResultTrainData.this);
        loading.loadDialog.show();
        init();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void init() {
        String intentList = getIntent().getStringExtra("list");
        String title =getIntent().getStringExtra("title");
        setTitle(title);
        list =  new Gson().fromJson(intentList,new TypeToken<ArrayList<TrainPojo>>(){}.getType());
        Log.d(TAG,new Gson().toJson(intentList));
        loading.loadDialog.dismiss();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new ResultTrainAdapter(ResultTrainData.this,list,title);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ResultTrainData.this));
    }
}
