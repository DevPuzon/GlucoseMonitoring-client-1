package com.softwaresolution.glucosemonitoringapp.UiPerson;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.softwaresolution.glucosemonitoringapp.Adapter.HistoryAdapter;
import com.softwaresolution.glucosemonitoringapp.Auth.PatientLoginForm;
import com.softwaresolution.glucosemonitoringapp.Auth.PersonLoginForm;
import com.softwaresolution.glucosemonitoringapp.Interactive.Loading;
import com.softwaresolution.glucosemonitoringapp.Pojo.ResultPojo;
import com.softwaresolution.glucosemonitoringapp.Pojo.UserAccount;
import com.softwaresolution.glucosemonitoringapp.R;
import com.softwaresolution.glucosemonitoringapp.UiPatient.History;

import java.util.ArrayList;

public class DoctorMain extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "DoctorMain";

    private TextView btn_logout;
    private ImageView img_doc_profile,img_reseta,img_chat,img_profile;
    private TextView txt_doc_name,txt_doc_email,txt_name,txt_email;
    private EditText edit_patient;
    private Button btn_enter;
    private RecyclerView recycler_list;
    private FirebaseFirestore db = FirebaseFirestore.getInstance(); 
    private HistoryAdapter adapter;
    private ArrayList<ResultPojo> list = new ArrayList<>();

    private Loading loading;
    private UserAccount personAccount ;
    private UserAccount patientacc;

    private SharedPreferences sharedDoc ;
    private SharedPreferences.Editor editor;
    private String person;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor__main);
        person = getIntent().getStringExtra("person");
        personAccount = PersonLoginForm.userProfile;
        loading = new Loading(DoctorMain.this);
        init();
        initPerson();
    }

    private void init() {
        sharedDoc = getSharedPreferences("sharedDoc", MODE_PRIVATE);
        editor = sharedDoc.edit();
        findViewById(R.id.main).setVisibility(View.GONE);
        edit_patient = (EditText) findViewById(R.id.edit_patient);
        img_doc_profile = (ImageView) findViewById(R.id.img_doc_profile);
        img_reseta = (ImageView) findViewById(R.id.img_reseta);
        if (person.equals("relative"))
            img_reseta.setVisibility(View.GONE);
        img_chat = (ImageView) findViewById(R.id.img_chat);
        img_profile = (ImageView) findViewById(R.id.img_profile);
        txt_doc_name = (TextView) findViewById(R.id.txt_doc_name);
        txt_doc_email = (TextView) findViewById(R.id.txt_doc_email);
        txt_name = (TextView) findViewById(R.id.txt_name);
        txt_email = (TextView) findViewById(R.id.txt_email);
        btn_enter = (Button) findViewById(R.id.btn_enter);
        recycler_list= (RecyclerView) findViewById(R.id.recycler_list);
        btn_logout = (TextView) findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(this);
        btn_enter.setOnClickListener(this);
        img_reseta.setOnClickListener(this);
        img_chat.setOnClickListener(this);
        
        adapter = new HistoryAdapter(DoctorMain.this,list);
        recycler_list.setAdapter(adapter);
        recycler_list.setHasFixedSize(true);
        recycler_list.setLayoutManager(new LinearLayoutManager(DoctorMain.this));

        String patientprofile= sharedDoc.getString("patientprofile"+person,"");
        if (!TextUtils.isEmpty(patientprofile)){
            patientacc = new Gson().fromJson(patientprofile,UserAccount.class);
            initProPatient();
        }
    }

    private void initPerson() {
        Glide.with(this).load(personAccount.getImg_url()).into(img_doc_profile);
        txt_doc_email.setText(personAccount.getEmail());
        txt_doc_name.setText(personAccount.getName());
    }

    private void initProPatient() {
        Glide.with(this).load(patientacc.getImg_url()).into(img_profile);
        txt_email.setText(patientacc.getEmail());
        txt_name.setText(patientacc.getName());
        edit_patient.setHint(patientacc.getEmail());

        findViewById(R.id.main).setVisibility(View.VISIBLE);
        initApi();
    }

    @Override
    public void onClick(View v) {
        if (v == btn_logout){
            FirebaseAuth.getInstance().signOut();
            this.finish();
            startActivity(new Intent(this, PatientLoginForm.class));
        }
        if (v == btn_enter){
            onEnter();
        }
        if (v == img_chat){
            onChat();
        }

        if (v == img_reseta){
            String patientuid = patientacc.getId();
            if (TextUtils.isEmpty(patientuid)){
                Toast.makeText(DoctorMain.this,"Please put the patient email",
                        Toast.LENGTH_LONG).show();
                return;
            }
            startActivity(new Intent(this,DocPrescription.class)
            .putExtra("patientuid",patientuid));
        }
    }

    private void onChat() {
        if (patientacc == null){
            Toast.makeText(DoctorMain.this,"Please put the patient email",
                    Toast.LENGTH_LONG).show();
            return;
        }
        startActivity(new Intent(this,PersonMsg.class)
                .putExtra("patientacc",new Gson().toJson(patientacc))
                .putExtra("person",person));
    }

    private void onEnter() {
        String email = edit_patient.getText().toString();
        db.collection("Glucose User account")
                .whereEqualTo("email",email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot doc : task.getResult()){
                                patientacc = doc.toObject(UserAccount.class);
                            }
                            if (patientacc == null){
                                Toast.makeText(DoctorMain.this,
                                        "No email address registered",
                                        Toast.LENGTH_LONG).show();
                                loading.loadDialog.dismiss();
                                return;
                            }
                            editor.putString("patientprofile"+person,new Gson().toJson(patientacc));
                            editor.apply();

                            initProPatient();
                        }else{
                            Toast.makeText(DoctorMain.this,task.getException().getLocalizedMessage(),
                                    Toast.LENGTH_LONG).show();
                            loading.loadDialog.dismiss();
                        }
                    }
                });
    }


    private void initApi() {
        db.collection("Glucose Result")
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
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}
