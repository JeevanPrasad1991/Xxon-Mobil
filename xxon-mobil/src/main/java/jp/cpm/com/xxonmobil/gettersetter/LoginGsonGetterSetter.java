package jp.cpm.com.xxonmobil.gettersetter;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jeevanp on 14-12-2017.
 */

public class LoginGsonGetterSetter {
    @SerializedName("Result")
    @Expose
    private List<Result> result = null;

    public List<Result> getResult() {
        return result;
    }
    public void setResult(List<Result> result) {
        this.result = result;
    }

    @SerializedName("Attendance_Status")
    @Expose
    private List<AttendanceStatus> attendanceStatus = null;

    public List<AttendanceStatus> getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(List<AttendanceStatus> attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }


}
