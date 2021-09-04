
package com.example.assets.Model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


public class Asset implements Serializable {

    @SerializedName("assetCode")
    @Expose
    private String assetCode;
    @SerializedName("assetName")
    @Expose
    private String assetName;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("specification")
    @Expose
    private String specification;
    @SerializedName("installedDate")
    @Expose
    private String installedDate;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("categoryPrefix")
    @Expose
    private String categoryPrefix;
    @SerializedName("categoryName")
    @Expose
    private String categoryName;
    @SerializedName("assignmentDTOs")
    @Expose
    private List<Assignment> assignmentDTOs = null;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getInstalledDate() {
        return installedDate;
    }

    public void setInstalledDate(String installedDate) {
        this.installedDate = installedDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCategoryPrefix() {
        return categoryPrefix;
    }

    public void setCategoryPrefix(String categoryPrefix) {
        this.categoryPrefix = categoryPrefix;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    public List<Assignment> getAssignmentDTOs() {
        return assignmentDTOs;
    }

    public void setAssignmentDTOs(List<Assignment> assignmentDTOs) {
        this.assignmentDTOs = assignmentDTOs;
    }
    public Asset(String assetName, String state, String specification, String installedDate, String categoryPrefix, String categoryName) {
        this.assetName = assetName;
        this.state = state;
        this.specification = specification;
        this.installedDate = installedDate;
        this.categoryPrefix = categoryPrefix;
        this.categoryName = categoryName;
    }

    public Asset() {
    }
}
