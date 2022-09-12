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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.softwaresolution.glucosemonitoringapp.Adapter.PrescriptionAdapter;
import com.softwaresolution.glucosemonitoringapp.Auth.PatientLoginForm;
import com.softwaresolution.glucosemonitoringapp.Interactive.Loading;
import com.softwaresolution.glucosemonitoringapp.Pojo.PrescriptionData;
import com.softwaresolution.glucosemonitoringapp.Pojo.ResultPojo;
import com.softwaresolution.glucosemonitoringapp.R;

import java.util.ArrayList;

public class Prescription extends AppCompatActivity {
    private String TAG = "Prescription";

    private ArrayList<PrescriptionData> list = new ArrayList<>();
    private PrescriptionAdapter adapter;
    private RecyclerView recyclerView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Loading loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_recycler);
        loading = new Loading(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        init();
        initData();
    }
    private void init() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new PrescriptionAdapter(this,list);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initData() {
        db.collection("Glucose prescription")
            .document(PatientLoginForm.userProfile.getId())
            .collection(PatientLoginForm.userProfile.getId())
            .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        loading.loadDialog.dismiss();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                PrescriptionData data = document.toObject(PrescriptionData.class);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Log.d(TAG, new Gson().toJson(data));
                                list.add(data);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
