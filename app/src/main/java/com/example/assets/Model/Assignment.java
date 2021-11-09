package com.example.assets.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Assignment implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("requestAssignId")
    @Expose
    private Integer requestAssignId;
    @SerializedName("assignedTo")
    @Expose
    private String assignedTo;
    @SerializedName("assignedBy")
    @Expose
    private String assignedBy;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("createdDate")
    @Expose
    private String createdDate;
    @SerializedName("updatedDate")
    @Expose
    private String updatedDate;
    @SerializedName("assignedDate")
    @Expose
    private String assignedDate;
    @SerializedName("intendedReturnDate")
    @Expose
    private String intendedReturnDate;
    @SerializedName("assignmentDetails")
    @Expose
    private List<AssignmentDetail> assignmentDetails = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRequestAssignId() {
        return requestAssignId;
    }

    public void setRequestAssignId(Integer requestAssignId) {
        this.requestAssignId = requestAssignId;
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

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(String assignedDate) {
        this.assignedDate = assignedDate;
    }

    public String getIntendedReturnDate() {
        return intendedReturnDate;
    }

    public void setIntendedReturnDate(String intendedReturnDate) {
        this.intendedReturnDate = intendedReturnDate;
    }

    public List<AssignmentDetail> getAssignmentDetails() {
        return assignmentDetails;
    }

    public void setAssignmentDetails(List<AssignmentDetail> assignmentDetails) {
        this.assignmentDetails = assignmentDetails;
    }
    public static class AssignmentDetail {

        @SerializedName("assetCode")
        @Expose
        private String assetCode;
        @SerializedName("assetName")
        @Expose
        private String assetName;
        @SerializedName("category")
        @Expose
        private String category;
        @SerializedName("specs")
        @Expose
        private String specs;
        @SerializedName("state")
        @Expose
        private String state;
        @SerializedName("returnedDate")
        @Expose
        private String returnedDate;

        public AssignmentDetail(String assetCode, String assetName, String category, String specs, String state, String returnedDate) {
            this.assetCode = assetCode;
            this.assetName = assetName;
            this.category = category;
            this.specs = specs;
            this.state = state;
            this.returnedDate = returnedDate;
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

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getSpecs() {
            return specs;
        }

        public void setSpecs(String specs) {
            this.specs = specs;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getReturnedDate() {
            return returnedDate;
        }

        public void setReturnedDate(String returnedDate) {
            this.returnedDate = returnedDate;
        }

    }
}