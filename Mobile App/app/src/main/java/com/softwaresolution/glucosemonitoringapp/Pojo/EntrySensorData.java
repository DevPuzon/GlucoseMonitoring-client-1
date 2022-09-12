package com.softwaresolution.glucosemonitoringapp.Pojo;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

public class EntrySensorData {
    private String name;
    private ArrayList<Entry> entryList;

    public EntrySensorData() {
    }

    public EntrySensorData(String name, ArrayList<Entry> entryList) {
        this.name = name;
        this.entryList = entryList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Entry> getEntryList() {
        return entryList;
    }

    public void setEntryList(ArrayList<Entry> entryList) {
        this.entryList = entryList;
    }

}