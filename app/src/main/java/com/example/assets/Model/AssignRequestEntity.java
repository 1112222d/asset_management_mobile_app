package com.example.assets.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AssignRequestEntity {

    @SerializedName("prefix")
    @Expose
    private String prefix;
    @SerializedName("note")
    @Expose
    private String note;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public AssignRequestEntity(String prefix, String note) {
        this.prefix = prefix;
        this.note = note;
    }
}
