package com.softwaresolution.glucosemonitoringapp.Pojo;

public class PatientProfile {
    private String uid;
    private String name;
    private String email;
    private String password;
    private String timestamp;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public PatientProfile() {
    }

    public PatientProfile(String uid, String name, String email, String password, String timestamp) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.password = password;
        this.timestamp = timestamp;
    }
}
