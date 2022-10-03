package com.softwaresolution.glucosemonitoringapp.Interactive;
import android.app.ProgressDialog;
import android.content.Context;

public class Loading {
    public ProgressDialog loadDialog;
    public String message ="Please wait";

    public void setMessage(String message) {
        this.message = message;
    }

    public Loading(Context context) {
        loadDialog =  new ProgressDialog(context);
        loadDialog.setTitle("Loading");
        loadDialog.setMessage(message);
        loadDialog.setCancelable(false);
    }
}
