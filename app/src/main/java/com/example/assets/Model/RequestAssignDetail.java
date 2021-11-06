package com.example.assets.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequestAssignDetail {

        @SerializedName("categoryId")
        @Expose
        private String categoryId;
        @SerializedName("quantity")
        @Expose
        private String quantity;

        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

}
