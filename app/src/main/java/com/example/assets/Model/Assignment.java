package com.example.assets.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Assignment implements Serializable {

    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("assetCode")
    @Expose
    private String assetCode;
    @SerializedName("assetName")
    @Expose
    private String assetName;
    @SerializedName("specfication")
    @Expose
    private String specfication;
    @SerializedName("assignedTo")
    @Expose
    private String assignedTo;
    @SerializedName("assignedBy")
    @Expose
    private String assignedBy;
    @SerializedName("assignedDate")
    @Expose
    private String assignedDate;
    @SerializedName("returnedDate")
    @Expose
    private Object returnedDate;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("isCreatedRequest")
    @Expose
    private Boolean isCreatedRequest;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getSpecfication() {
        return specfication;
    }

    public void setSpecfication(String specfication) {
        this.specfication = specfication;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }

    public String getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(String assignedDate) {
        this.assignedDate = assignedDate;
    }

    public Object getReturnedDate() {
        return returnedDate;
    }

    public void setReturnedDate(Object returnedDate) {
        this.returnedDate = returnedDate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Boolean getCreatedRequest() {
        return isCreatedRequest;
    }

    public void setCreatedRequest(Boolean createdRequest) {
        isCreatedRequest = createdRequest;
    }
}