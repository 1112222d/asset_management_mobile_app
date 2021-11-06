package com.example.assets.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AssignRequestEntity {

    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("intendedAssignDate")
    @Expose
    private String intendedAssignDate;
    @SerializedName("intendedReturnDate")
    @Expose
    private String intendedReturnDate;
    @SerializedName("requestAssignDetails")
    @Expose
    private List<RequestAssignDetail> requestAssignDetails = null;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getIntendedAssignDate() {
        return intendedAssignDate;
    }

    public void setIntendedAssignDate(String intendedAssignDate) {
        this.intendedAssignDate = intendedAssignDate;
    }

    public String getIntendedReturnDate() {
        return intendedReturnDate;
    }

    public void setIntendedReturnDate(String intendedReturnDate) {
        this.intendedReturnDate = intendedReturnDate;
    }

    public List<RequestAssignDetail> getRequestAssignDetails() {
        return requestAssignDetails;
    }

    public void setRequestAssignDetails(List<RequestAssignDetail> requestAssignDetails) {
        this.requestAssignDetails = requestAssignDetails;
    }

}

