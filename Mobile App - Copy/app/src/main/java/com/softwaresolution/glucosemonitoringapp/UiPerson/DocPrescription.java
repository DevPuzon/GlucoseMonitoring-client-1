package com.softwaresolution.glucosemonitoringapp.UiPerson;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.softwaresolution.glucosemonitoringapp.Auth.PersonLoginForm;
import com.softwaresolution.glucosemonitoringapp.Interactive.Loading;
import com.softwaresolution.glucosemonitoringapp.Pojo.PrescriptionData;
import com.softwaresolution.glucosemonitoringapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DocPrescription extends AppCompatActivity implements View.OnClickListener {

    private TextView btn_send;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String patientuid;
    private EditText txt_note;
    private Loading loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_prescription);
        loading = new Loading(this);
        patientuid = getIntent().getStringExtra("patientuid");
        init();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void init() {
        txt_note = (EditText) findViewById(R.id.txt_note);
        btn_send = (TextView) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (btn_send == v){
            onSend();
        }
    }

    private void onSend() {
        String note = txt_note.getText().toString();
        if (TextUtils.isEmpty(note)){
            Toast.makeText(DocPrescription.this,"Note is empty",
                    Toast.LENGTH_LONG).show();
            return;
        }
        loading.loadDialog.show();
        String getDocuTime = String.valueOf(new Date().getTime());
        String getTime = new SimpleDateFormat("hh:mm a",
                Locale.getDefault()).format(new Date());
        String dateToday = new SimpleDateFormat("MMM dd yyyy",
                Locale.getDefault()).format(new Date());
        PrescriptionData data  = new PrescriptionData(PersonLoginForm.userProfile.getId(),
                getDocuTime,txt_note.getText().toString(),dateToday+" "+getTime);
        db.collection("Glucose prescription")
                .document(patientuid)
                .collection(patientuid)
                .document(getDocuTime)
                .set(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loading.loadDialog.dismiss();
                        if (task.isSuccessful()){
                            Toast.makeText(DocPrescription.this,"Sent",
                                    Toast.LENGTH_LONG).show();
                            onBackPressed();
                        }else{
                            Toast.makeText(DocPrescription.this,task.getException().getLocalizedMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
