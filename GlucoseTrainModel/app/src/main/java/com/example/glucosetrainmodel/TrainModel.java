package com.example.glucosetrainmodel;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class TrainModel {
    private static String TAG = "TrainModelTAG";
    private static File myExternalFile;
    public static void saveData(String data){
        String filename = "GlucoseTrainModel.json";
        File root = Environment.getExternalStorageDirectory();
        File dir = new File ( root+"/GlucoseTrainModel");
        Log.d(TAG, "root path: "+ dir.getPath());
        Log.d(TAG, data);
        if (!dir.exists()){
            dir.mkdirs();
        }
        myExternalFile = new File(dir, filename);
        try {
            FileOutputStream f = new FileOutputStream(myExternalFile);
            PrintWriter pw = new PrintWriter(f);
            pw.print(data);
            pw.flush();
            pw.close();
            f.close();
            Log.d(TAG,"saved "+ myExternalFile.getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(TAG, "******* File not found. Did you" +
                    " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getModel(){
        String ret = "";
        String filename = "GlucoseTrainModel.json";
        File root = Environment.getExternalStorageDirectory();
        File dir = new File ( root+"/GlucoseTrainModel");
        Log.d(TAG, "root path: "+ dir.getPath());
        if(dir.exists()) {
            myExternalFile = new File(dir, filename);
            FileOutputStream os = null;
            StringBuilder text = new StringBuilder();
            try {
                BufferedReader br = new BufferedReader(new FileReader(myExternalFile));
                String line;
                while ((line = br.readLine()) != null) {
                    text.append(line);
                }
                br.close();
                Log.d(TAG,"done getting "+ myExternalFile.getPath());
            } catch (IOException e) {
                //You'll need to add proper error handling here
            }
            ret = text.toString();
            Log.d(TAG, String.valueOf(ret));
        }
        return ret;
    }
}
