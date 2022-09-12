package com.softwaresolution.glucosemonitoringapp.UiPerson;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.softwaresolution.glucosemonitoringapp.Auth.PatientLoginForm;
import com.softwaresolution.glucosemonitoringapp.R;

public class RelativeMain extends AppCompatActivity implements View.OnClickListener {

    private TextView btn_logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relative__main);
        init();
    }

    private void init() {
        btn_logout = (TextView) findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btn_logout){
            FirebaseAuth.getInstance().signOut();
            this.finish();
            startActivity(new Intent(this, PatientLoginForm.class));
        }
    }
}
