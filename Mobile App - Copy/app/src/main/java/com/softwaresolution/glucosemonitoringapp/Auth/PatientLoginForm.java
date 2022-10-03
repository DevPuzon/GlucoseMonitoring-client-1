package com.softwaresolution.glucosemonitoringapp.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.softwaresolution.glucosemonitoringapp.Interactive.Loading;
import com.softwaresolution.glucosemonitoringapp.MainActivity;
import com.softwaresolution.glucosemonitoringapp.Pojo.UserAccount;
import com.softwaresolution.glucosemonitoringapp.R;
import com.softwaresolution.glucosemonitoringapp.Utils.Permission;

public class PatientLoginForm extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "PatientLoginForm";
    private TextView btn_signdoc,btn_signgaurdian;
    private Button btn_login,btn_register;
    private EditText txt_email,txt_password;

    private static FirebaseAuth auth = FirebaseAuth.getInstance();
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static Loading loading;
    public static UserAccount userProfile;
    private static PatientLoginForm context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_patient_login_form);
        loading = new Loading(PatientLoginForm.this);
        context = PatientLoginForm.this;
        perimissioninit();
        init();
    }

    private void perimissioninit() {
        new Permission(this,this);
    }

    private void init() {
        txt_email = (EditText) findViewById(R.id.txt_email);
        txt_password = (EditText) findViewById(R.id.txt_password);

        btn_register = (Button) findViewById(R.id.btn_register);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_signdoc = (TextView) findViewById(R.id.btn_signdoc);
        btn_signgaurdian = (TextView) findViewById(R.id.btn_signgaurdian);
        btn_signdoc.setOnClickListener(this);
        btn_signgaurdian.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        btn_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (btn_login == v){
            //LOGIN
            onLogin();
        }
        if (btn_register == v){
            //REGISTER
            onRegister();
        }
        if (btn_signdoc == v){
            //SIGN DOCTOR
            startActivity(new Intent(this, PersonLoginForm.class)
                    .putExtra("person","doctor"));
        }
        if (btn_signgaurdian == v){
            //SIGN Relative
            startActivity(new Intent(this, PersonLoginForm.class)
                    .putExtra("person","relative"));
        }
    }

    private void onRegister() {
        startActivity(new Intent(this,RegisterForm.class));
    }
    private void onLogin() {
        String email = txt_email.getText().toString();
        String pass = txt_password.getText().toString();
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)){
            loading.loadDialog.show();
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                onStart();
                            }else {
                                Toast.makeText(PatientLoginForm.this,task.getException().getLocalizedMessage(),
                                        Toast.LENGTH_LONG).show();
                                loading.loadDialog.dismiss();
                            }
                        }
                    });
        }else{
            Toast.makeText(this,"Please fill all the data information",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        if (auth.getCurrentUser() != null){
            loading.loadDialog.show();
            GetUserProfile();
        }
        super.onStart();
    }

    public static void GetUserProfile(){
        db.collection("Glucose User account")
                .document(auth.getCurrentUser().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                loading.loadDialog.dismiss();
                if (documentSnapshot.toObject(UserAccount.class) == null){
                    context.startActivity(new Intent(context, PersonLoginForm.class));
                    return;
                }
                userProfile = documentSnapshot.toObject(UserAccount.class);
                context.finish();
                context.startActivity(new Intent(context, MainActivity.class));
            }
        });
    }
}
