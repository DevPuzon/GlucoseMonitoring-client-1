package com.example.glucosetrainmodel.Pojo;

public class SensorData {
//    22,4.63,1014.43,30.41,70.00,31.20,88.16,100.65,-

    private Float bgl;
    private Float volt;

    /**
     * No args constructor for use in serialization
     *
     */
    public SensorData() {
    }
    public SensorData(Float bgl, Float volt) {
        super();
        this.bgl = bgl;
        this.volt = volt;
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

}