package com.example.glucosetrainmodel.Pojo;

import java.util.ArrayList;

public class TrainPojo {
    private String name;
    private String status;//diabetic pre-diabetic normal
    private String createdAt;
    private SensorData sensorData;
    private ArrayList<EntrySensorData> entrilList;
    private String minPPm;

    public TrainPojo(String name, String status, String createdAt, SensorData sensorData, ArrayList<EntrySensorData> entrilList, String minPPm) {
        this.name = name;
        this.status = status;
        this.createdAt = createdAt;
        this.sensorData = sensorData;
        this.entrilList = entrilList;
        this.minPPm = minPPm;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public SensorData getSensorData() {
        return sensorData;
    }

    public void setSensorData(SensorData sensorData) {
        this.sensorData = sensorData;
    }

    public ArrayList<EntrySensorData> getEntrilList() {
        return entrilList;
    }

    public void setEntrilList(ArrayList<EntrySensorData> entrilList) {
        this.entrilList = entrilList;
    }

    public String getMinPPm() {
        return minPPm;
    }

    public void setMinPPm(String minPPm) {
        this.minPPm = minPPm;
    }
}
