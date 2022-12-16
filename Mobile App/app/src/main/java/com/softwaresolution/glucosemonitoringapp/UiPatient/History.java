package com.softwaresolution.glucosemonitoringapp.UiPatient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.softwaresolution.glucosemonitoringapp.Adapter.HistoryAdapter;
import com.softwaresolution.glucosemonitoringapp.Interactive.Loading;
import com.softwaresolution.glucosemonitoringapp.Pojo.ResultPojo;
import com.softwaresolution.glucosemonitoringapp.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class History extends AppCompatActivity {
    private String TAG = "History";
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private ArrayList<ResultPojo> list = new ArrayList<>();

    private static FirebaseAuth auth = FirebaseAuth.getInstance();
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
        firestore.collection("Glucose Result Client 1")
                .document(auth.getCurrentUser().getUid())
                .collection("Glucose Result Client 1")
                .orderBy("timeStampId", Query.Direction.DESCENDING)
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
//                            Arrays.sort(list, new Comparator< ResultPojo >() {
                            Collections.sort(list, new Comparator<ResultPojo>() {
                                @Override
                                public int compare(ResultPojo first, ResultPojo second) {
                                    long firstStamp = 0;
                                    long secondStamp = 0;
                                    DateFormat format = new SimpleDateFormat("dd-MMM-yyyy  HH:mm:ss");
                                    try {
                                        firstStamp = format.parse(first.getTimeStampId()).getTime();
                                        secondStamp = format.parse(second.getTimeStampId()).getTime();
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    Log.d(TAG,  "firstStamp "+ String.valueOf(firstStamp));
                                    Log.d(TAG,  "secondStamp "+ String.valueOf(secondStamp));
                                    return Integer.valueOf(String.valueOf(Long.valueOf(secondStamp).compareTo(firstStamp)));
                                }
                            });
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
