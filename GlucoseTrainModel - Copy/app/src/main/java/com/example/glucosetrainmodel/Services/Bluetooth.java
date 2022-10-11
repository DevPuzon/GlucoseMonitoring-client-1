package com.example.glucosetrainmodel.Services;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.glucosetrainmodel.Pojo.SensorData;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;
public class Bluetooth  extends Thread{

    private String TAG = "TAGBluetooth";

    String address = null , name=null;

    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    Set<BluetoothDevice> pairedDevices;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Context context;
    private IBluetooth iBluetooth;

    public Bluetooth(Context context,IBluetooth iBluetooth) {
        this.context = context;
        this.iBluetooth = iBluetooth;
        try {
            bluetooth_connect_device();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void bluetooth_connect_device() throws IOException {
        try
        {
            Log.d(TAG,"start" );
            myBluetooth = BluetoothAdapter.getDefaultAdapter();
            address = myBluetooth.getAddress();
            pairedDevices = myBluetooth.getBondedDevices();
            if (pairedDevices.size()>0)
            {
                for(BluetoothDevice bt : pairedDevices)
                {
                    address=bt.getAddress().toString();
                    name = bt.getName().toString();
                    Toast.makeText(context,"Connected", Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"loop name"+name+"loop address Address: "+address );
                }
            }else{
                Toast.makeText(context,"Bluetooth not paired, please pair the blutooth HC-05 and refresh the application"
                        ,Toast.LENGTH_LONG).show();
            }
            Log.d(TAG, String.valueOf(pairedDevices.size()));

        }
        catch(Exception ex){
            Log.d(TAG,ex.getLocalizedMessage());
        }
        myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
        BluetoothDevice dispositivo = myBluetooth.getRemoteDevice("00:21:13:00:44:66");//connects to the device's address and checks if it's available
        btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
        mmInputStream = btSocket.getInputStream();
        btSocket.connect();
        beginListenForData();
        try {
            Log.d(TAG,"BT Name: "+name+"\nBT Address: "+address );
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    private void sendSensorData(String data) {
        int endOfLineIndex = data.indexOf("-");
        Log.d(TAG,"DATA "+ data);
        if (endOfLineIndex > 0) {
            SensorData sensorData;
            String[] datas = data.split(",");
            Log.d(TAG,"size "+datas.length);
            if (datas.length == 9){
//            mq3_ppm+","+mq3_kohm+","+bmp_pressure+","+bmp_temperature+","+dht_humidity+","+dht_celcius+","+dht_fahrenheit+","+dht_heatindex+",
                try{
                    sensorData = new SensorData(Integer.parseInt(datas[0]),
                            Float.parseFloat(datas[1]),
                            Float.parseFloat(datas[2]),
                            Float.parseFloat(datas[3]),
                            Float.parseFloat(datas[4]),
                            Float.parseFloat(datas[5]),
                            Float.parseFloat(datas[6]),
                            Float.parseFloat(datas[7]));
                    Log.d(TAG, new Gson().toJson(sensorData));
                    iBluetooth.getData(sensorData);
                }catch (Exception e){
                    Log.d(TAG,"/////");
                    iBluetooth.calibrating();
                }
            }
        }
    }
    volatile boolean stopWorker;
    int readBufferPosition;
    byte[] readBuffer;
    Thread workerThread;
    InputStream mmInputStream;


    private void beginListenForData() {
        final Handler
                handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;
                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            sendSensorData(data);
                                        }
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    public interface IBluetooth{
        void getData(SensorData data);
        void calibrating();
    }

}
