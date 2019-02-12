
package jp.cpm.com.xxonmobil.gsonGetterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NonWorkingReason {
    @SerializedName("Reason_Id")
    @Expose
    private Integer reasonId;
    @SerializedName("Reason")
    @Expose
    private String reason;
    @SerializedName("Entry_Allow")
    @Expose
    private Boolean entryAllow;
    @SerializedName("Image_Allow")
    @Expose
    private Boolean imageAllow;
    @SerializedName("GPS_Mandatory")
    @Expose
    private Boolean gPSMandatory;

    public Integer getReasonId() {
        return reasonId;
    }

    public void setReasonId(Integer reasonId) {
        this.reasonId = reasonId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Boolean getEntryAllow() {
        return entryAllow;
    }

    public void setEntryAllow(Boolean entryAllow) {
        this.entryAllow = entryAllow;
    }

    public Boolean getImageAllow() {
        return imageAllow;
    }

    public void setImageAllow(Boolean imageAllow) {
        this.imageAllow = imageAllow;
    }

    public Boolean getGPSMandatory() {
        return gPSMandatory;
    }

    public void setGPSMandatory(Boolean gPSMandatory) {
        this.gPSMandatory = gPSMandatory;
    }

    @SerializedName("For_Coverage")
    @Expose
    private Boolean forCoverage;
    @SerializedName("For_Attendance")
    @Expose
    private Boolean forAttendance;
    public Boolean getForCoverage() {
        return forCoverage;
    }

    public void setForCoverage(Boolean forCoverage) {
        this.forCoverage = forCoverage;
    }

    public Boolean getForAttendance() {
        return forAttendance;
    }

    public void setForAttendance(Boolean forAttendance) {
        this.forAttendance = forAttendance;
    }
}
