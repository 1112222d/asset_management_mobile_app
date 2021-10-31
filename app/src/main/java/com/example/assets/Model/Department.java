package com.example.assets.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Department {
    @SerializedName("deptCode")
    @Expose
    private String deptCode;
    @SerializedName("name")
    @Expose
    private String name;

    public Department() {
    }

    public Department(String deptCode, String name) {
        this.deptCode = deptCode;
        this.name = name;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
