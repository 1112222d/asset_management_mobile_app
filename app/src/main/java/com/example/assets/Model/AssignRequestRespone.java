package com.example.assets.Model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AssignRequestRespone implements Serializable {


        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("prefix")
        @Expose
        private String prefix;
        @SerializedName("category")
        @Expose
        private String category;
        @SerializedName("note")
        @Expose
        private String note;
        @SerializedName("state")
        @Expose
        private String state;
        @SerializedName("requestedDate")
        @Expose
        private String requestedDate;
        @SerializedName("requestedBy")
        @Expose
        private String requestedBy;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
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

        public String getRequestedBy() {
            return requestedBy;
        }

        public void setRequestedBy(String requestedBy) {
            this.requestedBy = requestedBy;
        }

}
