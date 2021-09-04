package com.example.assets.Model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class Report {

    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("total")
    @Expose
    private Integer total;
    @SerializedName("assigned")
    @Expose
    private Integer assigned;
    @SerializedName("available")
    @Expose
    private Integer available;
    @SerializedName("notAvailable")
    @Expose
    private Integer notAvailable;
    @SerializedName("waitingForRecycle")
    @Expose
    private Integer waitingForRecycle;
    @SerializedName("recycled")
    @Expose
    private Integer recycled;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getAssigned() {
        return assigned;
    }

    public void setAssigned(Integer assigned) {
        this.assigned = assigned;
    }

    public Integer getAvailable() {
        return available;
    }

    public void setAvailable(Integer available) {
        this.available = available;
    }

    public Integer getNotAvailable() {
        return notAvailable;
    }

    public void setNotAvailable(Integer notAvailable) {
        this.notAvailable = notAvailable;
    }

    public Integer getWaitingForRecycle() {
        return waitingForRecycle;
    }

    public void setWaitingForRecycle(Integer waitingForRecycle) {
        this.waitingForRecycle = waitingForRecycle;
    }

    public Integer getRecycled() {
        return recycled;
    }

    public void setRecycled(Integer recycled) {
        this.recycled = recycled;
    }
}
