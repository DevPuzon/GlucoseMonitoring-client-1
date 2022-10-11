package com.example.glucosetrainmodel.Pojo;

public class PrescriptionData {
    private String docuid;
    private String document;
    private String note;
    private String createAt;

    public PrescriptionData(String docuid, String document, String note, String createAt) {
        this.docuid = docuid;
        this.document = document;
        this.note = note;
        this.createAt = createAt;
    }

    public String getDocuid() {
        return docuid;
    }

    public void setDocuid(String docuid) {
        this.docuid = docuid;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public PrescriptionData() {
    }
}
