package com.softwaresolution.glucosemonitoringapp.Pojo;

import java.util.ArrayList;
public class ConvoData {
    private String Time;

    private String From;
    private String FromUid;
    private String Message;
    private String CreatedAt;

    public ConvoData() {
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getFrom() {
        return From;
    }

    public void setFrom(String from) {
        From = from;
    }

    public String getFromUid() {
        return FromUid;
    }

    public void setFromUid(String fromUid) {
        FromUid = fromUid;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        CreatedAt = createdAt;
    }

    public ConvoData(String time, String from, String fromUid, String message, String createdAt) {
        Time = time;
        From = from;
        FromUid = fromUid;
        Message = message;
        CreatedAt = createdAt;
    }
}
