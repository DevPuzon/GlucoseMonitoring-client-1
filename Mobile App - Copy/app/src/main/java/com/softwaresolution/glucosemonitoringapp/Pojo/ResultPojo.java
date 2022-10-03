package com.softwaresolution.glucosemonitoringapp.Pojo;
//var ppm = 25;
//var atHy = 1.00784; // atomic mass of hydrogen.
//var gluHyMass = 12.12; //glucose hydroge mass
//
//var ppmGrams = (ppm * gluHyMass) /1000000; // ppm to grams
//console.log("PPM Grams", ppmGrams);
//var ppmMoles = ppmGrams / atHy ; // ppm grams to moles
//console.log("PPM Moles", ppmMoles);
//var mmols = ppmMoles * 1000000 ; // mol t mmol
//console.log("mmol", mmols);

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
