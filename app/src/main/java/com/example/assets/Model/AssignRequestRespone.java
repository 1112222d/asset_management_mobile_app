package com.example.assets.Model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class AssignRequestRespone implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("requestedDate")
    @Expose
    private String requestedDate;
    @SerializedName("updatedDate")
    @Expose
    private Object updatedDate;
    @SerializedName("intendedAssignDate")
    @Expose
    private String intendedAssignDate;
    @SerializedName("intendedReturnDate")
    @Expose
    private String intendedReturnDate;
    @SerializedName("requestedBy")
    @Expose
    private String requestedBy;
    @SerializedName("requestAssignDetails")
    @Expose
    private List<RequestAssignDetailRS> requestAssignDetails = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(String requestedDate) {
        this.requestedDate = requestedDate;
    }

    public Object getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Object updatedDate) {
        this.updatedDate = updatedDate;
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

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public List<RequestAssignDetailRS> getRequestAssignDetails() {
        return requestAssignDetails;
    }

    public void setRequestAssignDetails(List<RequestAssignDetailRS> requestAssignDetails) {
        this.requestAssignDetails = requestAssignDetails;
    }
    public class RequestAssignDetailRS implements Serializable
    {
        @SerializedName("requestAssignId")
        @Expose
        private Integer requestAssignId;
        @SerializedName("categoryId")
        @Expose
        private String categoryId;
        @SerializedName("categoryName")
        @Expose
        private String categoryName;
        @SerializedName("quantity")
        @Expose
        private Integer quantity;

        public Integer getRequestAssignId() {
            return requestAssignId;
        }

        public void setRequestAssignId(Integer requestAssignId) {
            this.requestAssignId = requestAssignId;
        }

        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}
