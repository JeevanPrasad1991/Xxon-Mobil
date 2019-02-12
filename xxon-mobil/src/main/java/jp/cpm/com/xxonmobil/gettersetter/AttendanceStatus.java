package jp.cpm.com.xxonmobil.gettersetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AttendanceStatus {
    @SerializedName("Reason_Id")
    @Expose
    private Integer reasonId;

    public Integer getReasonId() {
        return reasonId;
    }

    public void setReasonId(Integer reasonId) {
        this.reasonId = reasonId;
    }

}
