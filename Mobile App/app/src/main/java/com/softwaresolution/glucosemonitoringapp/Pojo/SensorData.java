package com.softwaresolution.glucosemonitoringapp.Pojo;

public class SensorData {
//    22,4.63,1014.43,30.41,70.00,31.20,88.16,100.65,-

    private Float bgl;
    private Float volt;
    private boolean isCalibrated;

    /**
     * No args constructor for use in serialization
     *
     */
    public SensorData() {
    }
    public SensorData(Float bgl, Float volt,boolean isCalibrated) {
        super();
        this.bgl = bgl;
        this.volt = volt;
        this.isCalibrated = isCalibrated;
    }

    public Float getBgl() {
        return bgl;
    }

    public void setBgl(Float bgl) {
        this.bgl = bgl;
    }

    public Float getVolt() {
        return volt;
    }

    public void setVolt(Float volt) {
        this.volt = volt;
    }

    public boolean getCalibrated() {
        return isCalibrated;
    }

    public void setIsCalibrated(boolean isCalibrated) {
        this.isCalibrated = isCalibrated;
    }
}