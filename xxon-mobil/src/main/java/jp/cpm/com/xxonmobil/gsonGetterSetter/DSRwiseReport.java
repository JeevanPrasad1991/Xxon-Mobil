package jp.cpm.com.xxonmobil.gsonGetterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DSRwiseReport {
    @SerializedName("DSR_Code")
    @Expose
    private String dSRCode;
    @SerializedName("Target_Outlet")
    @Expose
    private Integer targetOutlet;
    @SerializedName("Merchandised")
    @Expose
    private Integer merchandised;
    @SerializedName("Outlet_Ach")
    @Expose
    private Integer outletAch;
    @SerializedName("Outlet_Perfect")
    @Expose
    private Integer outletPerfect;
    @SerializedName("Perfect_Ach")
    @Expose
    private Integer perfectAch;

    public String getDSRCode() {
        return dSRCode;
    }

    public void setDSRCode(String dSRCode) {
        this.dSRCode = dSRCode;
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

    public Integer getOutletAch() {
        return outletAch;
    }

    public void setOutletAch(Integer outletAch) {
        this.outletAch = outletAch;
    }

    public Integer getOutletPerfect() {
        return outletPerfect;
    }

    public void setOutletPerfect(Integer outletPerfect) {
        this.outletPerfect = outletPerfect;
    }

    public Integer getPerfectAch() {
        return perfectAch;
    }

    public void setPerfectAch(Integer perfectAch) {
        this.perfectAch = perfectAch;
    }
}
