package com.softwaresolution.glucosemonitoringapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.softwaresolution.glucosemonitoringapp.Auth.PatientLoginForm;
import com.softwaresolution.glucosemonitoringapp.Pojo.UserAccount;
import com.softwaresolution.glucosemonitoringapp.UiPatient.History;
import com.softwaresolution.glucosemonitoringapp.UiPatient.Home;
import com.softwaresolution.glucosemonitoringapp.UiPatient.Message;
import com.softwaresolution.glucosemonitoringapp.UiPatient.Prescription;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Home.IHome {
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initProfile();

    }

    private void initProfile() {
        View headerView = navigationView.getHeaderView(0);
        TextView  txt_email = (TextView) headerView.findViewById(R.id.txt_email);
        TextView txt_name = (TextView ) headerView.findViewById(R.id.txt_name);
        ImageView img_profile = (ImageView) headerView.findViewById(R.id.imageView);

        UserAccount userAccount = PatientLoginForm.userProfile;
        txt_name.setText(userAccount.getName());
        txt_email.setText(userAccount.getEmail());
        Glide.with(MainActivity.this)
                .load(userAccount.getImg_url())
                .centerCrop()
                .into(img_profile);
    }

    private void init() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new Home()).commit();

        navigationView =(NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout = findViewById(R.id.drawer_layout);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case R.id.nav_doctormessage:
                startActivity(new Intent(MainActivity.this, Message.class)
                .putExtra("person","doctor"));
                break;
            case R.id.nav_relativemessage:
                startActivity(new Intent(MainActivity.this, Message.class)
                        .putExtra("person","relative"));
                break;
            case R.id.nav_history:
                startActivity(new Intent(MainActivity.this, History.class));
                break;
            case R.id.nav_pres:
                startActivity(new Intent(MainActivity.this, Prescription.class));
                break;
            case R.id.nav_logout:
                this.finish();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, PatientLoginForm.class));
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.END);
        return false;
    }

    @Override
    public void onClickMenu() {
        drawerLayout.openDrawer(GravityCompat.END);
    }

    @Override
    public void onBackPressed() {
        if (false)
            super.onBackPressed();
    }
}
