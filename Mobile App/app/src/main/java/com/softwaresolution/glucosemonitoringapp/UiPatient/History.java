package com.softwaresolution.glucosemonitoringapp.UiPatient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.softwaresolution.glucosemonitoringapp.Adapter.HistoryAdapter;
import com.softwaresolution.glucosemonitoringapp.Interactive.Loading;
import com.softwaresolution.glucosemonitoringapp.Pojo.ResultPojo;
import com.softwaresolution.glucosemonitoringapp.R;

import java.util.ArrayList;

public class History extends AppCompatActivity {
    private String TAG = "History";
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private ArrayList<ResultPojo> list = new ArrayList<>();

    private Loading loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_recycler);
        loading = new Loading(History.this);
        loading.loadDialog.show();
        init();
        initApi();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initApi() {
        firestore.collection("Glucose Result Client 1").orderBy("timeStampId", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        loading.loadDialog.dismiss();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ResultPojo resultPojo = document.toObject(ResultPojo.class);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Log.d(TAG, new Gson().toJson(resultPojo));
                                list.add(resultPojo);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void init() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new HistoryAdapter(History.this,list);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(History.this));
    }
}
