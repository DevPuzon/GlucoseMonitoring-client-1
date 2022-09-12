package com.softwaresolution.glucosemonitoringapp.Pojo;

import java.util.ArrayList;

public class ResultPojo {
    private SensorData mainData;
    private ArrayList<EntrySensorData> sensorDatas = new ArrayList<>();
    private String TimeStampId;
    private String minPPm;
    private String status;
    public ResultPojo() {
    }

    public SensorData getMainData() {
        return mainData;
    }

    public void setMainData(SensorData mainData) {
        this.mainData = mainData;
    }

    public ArrayList<EntrySensorData> getSensorDatas() {
        return sensorDatas;
    }

    public void setSensorDatas(ArrayList<EntrySensorData> sensorDatas) {
        this.sensorDatas = sensorDatas;
    }

    public String getTimeStampId() {
        return TimeStampId;
    }

    public void setTimeStampId(String timeStampId) {
        TimeStampId = timeStampId;
    }

    public String getMinPPm() {
        return minPPm;
    }

    public void setMinPPm(String minPPm) {
        this.minPPm = minPPm;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ResultPojo(SensorData mainData, ArrayList<EntrySensorData> sensorDatas, String timeStampId, String minPPm, String status) {
        this.mainData = mainData;
        this.sensorDatas = sensorDatas;
        TimeStampId = timeStampId;
        this.minPPm = minPPm;
        this.status = status;
    }
}
