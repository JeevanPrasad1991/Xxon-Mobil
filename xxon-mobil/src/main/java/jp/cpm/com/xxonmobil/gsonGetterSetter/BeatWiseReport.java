package jp.cpm.com.xxonmobil.gsonGetterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BeatWiseReport {
    @SerializedName("Beat_Name")
    @Expose
    private String beatName;
    @SerializedName("Target_Outlet")
    @Expose
    private Integer targetOutlet;
    @SerializedName("Merchandised")
    @Expose
    private Integer merchandised;
    @SerializedName("Outlet_Ach")
    @Expose
    private Double outletAch;
    @SerializedName("Outlet_Perfect")
    @Expose
    private Integer outletPerfect;
    @SerializedName("Perfect_Ach")
    @Expose
    private Double pefectAch;

    public String getBeatName() {
        return beatName;
    }

    public void setBeatName(String beatName) {
        this.beatName = beatName;
    }

    public Integer getTargetOutlet() {
        return targetOutlet;
    }

    public void setTargetOutlet(Integer targetOutlet) {
        this.targetOutlet = targetOutlet;
    }

    public Integer getMerchandised() {
        return merchandised;
    }

    public void setMerchandised(Integer merchandised) {
        this.merchandised = merchandised;
    }

    public Double getOutletAch() {
        return outletAch;
    }

    public void setOutletAch(Double outletAch) {
        this.outletAch = outletAch;
    }

    public Integer getOutletPerfect() {
        return outletPerfect;
    }

    public void setOutletPerfect(Integer outletPerfect) {
        this.outletPerfect = outletPerfect;
    }

    public Double getPefectAch() {
        return pefectAch;
    }

    public void setPefectAch(Double pefectAch) {
        this.pefectAch = pefectAch;
    }
}
