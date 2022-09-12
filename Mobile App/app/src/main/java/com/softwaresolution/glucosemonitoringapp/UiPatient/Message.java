package com.softwaresolution.glucosemonitoringapp.UiPatient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.softwaresolution.glucosemonitoringapp.Adapter.ConvoAdapter;
import com.softwaresolution.glucosemonitoringapp.Auth.PatientLoginForm;
import com.softwaresolution.glucosemonitoringapp.Auth.RegisterForm;
import com.softwaresolution.glucosemonitoringapp.Pojo.ConvoData;
import com.softwaresolution.glucosemonitoringapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Message extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "Message";

    private RecyclerView recycler_list;
    private ImageView img_send;
    private EditText edit_message;

    private ArrayList<Object> convoDatas = new ArrayList<>();
    private ConvoAdapter adapter;
    private String person = "";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_message);
        person= getIntent().getStringExtra("person");
        setTitle("To "+person);
        init();
        if (person.equals("doctor")){
            convoInitDoctor();
        }
        if (person.equals("relative")){
            convoInitRelative();
        }
    }

    private void init() {
        recycler_list = (RecyclerView) findViewById(R.id.recycler_list);
        edit_message = (EditText) findViewById(R.id.edit_message);
        img_send = (ImageView) findViewById(R.id.img_send);
        img_send.setOnClickListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private void convoInitRelative() {
        adapter = new ConvoAdapter(this,convoDatas,PatientLoginForm.userProfile);
        recycler_list.setAdapter(adapter);
        recycler_list.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recycler_list.setLayoutManager(mLayoutManager);

        db.collection("Glucose Patient Relative")
                .document(PatientLoginForm.userProfile.getId())
                .collection(PatientLoginForm.userProfile.getId())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    ConvoData convo = doc.toObject(ConvoData.class);
                    convoDatas.add(convo);
                }
                adapter.notifyDataSetChanged();
            }
        }) ;
    }

    private void convoInitDoctor() {
        adapter = new ConvoAdapter(this,convoDatas,PatientLoginForm.userProfile);
        recycler_list.setAdapter(adapter);
        recycler_list.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recycler_list.setLayoutManager(mLayoutManager);

        db.collection("Glucose Patient Doctor")
                .document(PatientLoginForm.userProfile.getId())
                .collection(PatientLoginForm.userProfile.getId())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    ConvoData convo = doc.toObject(ConvoData.class);
                    convoDatas.add(convo);
                }
                adapter.notifyDataSetChanged();
            }
        }) ;
    }

    @Override
    public void onClick(View v) {
        if (v == img_send){
            String msg = edit_message.getText().toString();
            if (TextUtils.isEmpty(msg))
                return;
            if (person.equals("doctor")){
                onSendMsgDotor(msg);
            }
            if (person.equals("relative")){
                onSendMsgRelative(msg);
            }
            edit_message.setText("");
        }
    }

    private void onSendMsgRelative(String msg) {
        String getDocuTime = String.valueOf(new Date().getTime());
        String getTime = new SimpleDateFormat("hh:mm a",
                Locale.getDefault()).format(new Date());
        String dateToday = new SimpleDateFormat("MMM dd yyyy",
                Locale.getDefault()).format(new Date());

        final ConvoData data = new ConvoData(getTime,PatientLoginForm.userProfile.getName()
                ,PatientLoginForm.userProfile.getId(),msg,dateToday);
        db.collection("Glucose Patient Relative")
                .document(PatientLoginForm.userProfile.getId())
                .collection(PatientLoginForm.userProfile.getId())
                .document(getDocuTime)
                .set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    convoDatas.add(data);
                    adapter.notifyDataSetChanged();
                }else{
                    Log.d(TAG, ":failure", task.getException());
                    Toast.makeText(Message.this, " failed. "+ task.getException().getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void onSendMsgDotor(String msg) {
        String getDocuTime = String.valueOf(new Date().getTime());
        String getTime = new SimpleDateFormat("hh:mm a",
                Locale.getDefault()).format(new Date());

        String dateToday = new SimpleDateFormat("MMM dd yyyy",
                Locale.getDefault()).format(new Date());

        final ConvoData data = new ConvoData(getTime,PatientLoginForm.userProfile.getName()
        ,PatientLoginForm.userProfile.getId(),msg,dateToday);
        db.collection("Glucose Patient Doctor")
                .document(PatientLoginForm.userProfile.getId())
                .collection(PatientLoginForm.userProfile.getId())
                .document(getDocuTime)
                .set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    convoDatas.add(data);
                    adapter.notifyDataSetChanged();
                }else{
                    Log.d(TAG, ":failure", task.getException());
                    Toast.makeText(Message.this, " failed. "+ task.getException().getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
