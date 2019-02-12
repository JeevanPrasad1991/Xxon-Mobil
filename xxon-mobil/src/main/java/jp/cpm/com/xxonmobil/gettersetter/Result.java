
package jp.cpm.com.xxonmobil.gettersetter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class Result {
    @SerializedName("Rightname")
    @Expose
    private String rightname;
    @SerializedName("App_Version")
    @Expose
    private Integer appVersion;
    @SerializedName("App_Path")
    @Expose
    private String appPath;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("Currentdate")
    @Expose
    private String currentdate;
    @SerializedName("Geo_Fencing")
    @Expose
    private Integer geoFencing;

    public String getNotice_board() {
        return notice_board;
    }

    public void setNotice_board(String notice_board) {
        this.notice_board = notice_board;
    }

    //for notice board url
    @SerializedName("Notice_Url")
    @Expose
    private String notice_board;

    public String getRightname() {
        return rightname;
    }

    public void setRightname(String rightname) {
        this.rightname = rightname;
    }

    public Integer getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(Integer appVersion) {
        this.appVersion = appVersion;
    }

    public String getAppPath() {
        return appPath;
    }

    public void setAppPath(String appPath) {
        this.appPath = appPath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrentdate() {
        return currentdate;
    }

    public void setCurrentdate(String currentdate) {
        this.currentdate = currentdate;
    }

    public Integer getGeoFencing() {
        return geoFencing;
    }

    public void setGeoFencing(Integer geoFencing) {
        this.geoFencing = geoFencing;
    }

}
