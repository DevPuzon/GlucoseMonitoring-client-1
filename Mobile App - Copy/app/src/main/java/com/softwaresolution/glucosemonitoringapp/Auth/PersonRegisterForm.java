package com.softwaresolution.glucosemonitoringapp.Auth;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.softwaresolution.glucosemonitoringapp.Interactive.DialogGetPicture;
import com.softwaresolution.glucosemonitoringapp.Interactive.Loading;
import com.softwaresolution.glucosemonitoringapp.Pojo.UserAccount;
import com.softwaresolution.glucosemonitoringapp.R;
import com.softwaresolution.glucosemonitoringapp.Utils.BitmapUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PersonRegisterForm extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "PersonRegisterForm";
    private ImageView img_profile;
    private EditText txt_fullname,txt_email,txt_password,txt_confirm;
    private Button btn_register;
    private Loading loading;
    private Bitmap bitmapProfile;

    private String person="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_register_form);
        person = getIntent().getStringExtra("person");
        loading = new Loading(PersonRegisterForm.this);
        init();
    }

    private void init() {
        img_profile = (ImageView)findViewById(R.id.img_profile);
        img_profile.setOnClickListener(PersonRegisterForm.this);
        txt_fullname = (EditText) findViewById(R.id.txt_fullname);
        txt_email = (EditText) findViewById(R.id.txt_email);
        txt_password = (EditText) findViewById(R.id.txt_password);
        txt_confirm = (EditText) findViewById(R.id.txt_confirm);
        btn_register = (Button) findViewById(R.id.btn_register);
        btn_register.setOnClickListener(PersonRegisterForm.this);
        TextView txt_person = (TextView)findViewById(R.id.txt_person);
        txt_person.setText(person.substring(0, 1).toUpperCase() + person.substring(1));
    }

    @Override
    public void onClick(View v) {
        if(img_profile == v){
            onImgProfile();
        }
        if (btn_register == v){
            onRegister();
        }
    }

    private void onRegister() {
        String fullname = txt_fullname.getText().toString();
        String password = txt_password.getText().toString();
        String email = txt_email.getText().toString();
        String confirm = txt_confirm.getText().toString();
        if (!TextUtils.isEmpty(fullname) &&
                !TextUtils.isEmpty(email) &&
                !TextUtils.isEmpty(password) &&
                !TextUtils.isEmpty(confirm) &&
                bitmapProfile != null){
            if (password.length() < 7){
                Toast.makeText(PersonRegisterForm.this,"Password must 7 characters"
                        ,Toast.LENGTH_LONG).show();
                return;
            }
            if (!password.equals(confirm)){
                Toast.makeText(PersonRegisterForm.this,"Password and confirm password doesn't match"
                        ,Toast.LENGTH_LONG).show();
                return;
            }
            onSaveAccount();
        }else {
            Toast.makeText(PersonRegisterForm.this,"Please fill up all data provided and the your profile picture."
                    ,Toast.LENGTH_LONG).show();
        }
    }

    private void onImgProfile() {
        DialogGetPicture alertDialog = new DialogGetPicture();
        alertDialog.show(getSupportFragmentManager(), "fragment_alert");

        alertDialog.setOnGetPicture(new DialogGetPicture.IDialogGetPicture() {
            @Override
            public void onGetPicture(String decodeBitmap) {
                bitmapProfile = BitmapUtils.decodeBase64(decodeBitmap);
                img_profile.setImageBitmap(bitmapProfile);
                Log.d(TAG,"Picture bimap "+ decodeBitmap);
            }
        });
    }

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private void onSaveAccount() {
        loading.loadDialog.show();
        String fullname = txt_fullname.getText().toString();
        String password = txt_password.getText().toString();
        String email = txt_email.getText().toString();
        String confirm = txt_confirm.getText().toString();
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(PersonRegisterForm.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            saveAccountDb(task.getResult().getUser().getUid());
                        }else {
                            // If sign in fails, display a message to the user.
                            Log.d(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(PersonRegisterForm.this, "Authentication failed. "+ task.getException().getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                            loading.loadDialog.dismiss();
                        }
                    }
                });
    }

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private StorageReference reference = FirebaseStorage.getInstance().getReference();
    private void saveAccountDb(final String user) {

        Uri file = BitmapUtils.getImageUri(PersonRegisterForm.this,bitmapProfile);
        final StorageReference storageReference = reference.child("Glucose "+person+" account");

        storageReference.putFile(file).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Log.d(TAG,"url "+task.getResult().toString());
                if (task.isSuccessful()){
                    String url = task.getResult().toString();
                    saveAuthDB(url,user);
                }else {
                    Toast.makeText(PersonRegisterForm.this, "Authentication failed. "+ task.getException().getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();
                    loading.loadDialog.dismiss();
                }
            }
        });
    }

    Date c = Calendar.getInstance().getTime();
    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy  HH:mm:ss");
    private void saveAuthDB(String url,String uid) {
        String formattedDate = df.format(c);
        String fullname = txt_fullname.getText().toString();
        String email = txt_email.getText().toString();

        UserAccount account = new UserAccount(uid,fullname,email,formattedDate
                ,url);
        firestore.collection("Glucose "+person+" account").
                document(uid).set(account).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loading.loadDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(PersonRegisterForm.this,"Saved",Toast.LENGTH_LONG).show();
                            onBackPressed();
                        }else{
                            Toast.makeText(PersonRegisterForm.this,"Authentication failed. "+ task.getException().getLocalizedMessage()
                                    ,Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
