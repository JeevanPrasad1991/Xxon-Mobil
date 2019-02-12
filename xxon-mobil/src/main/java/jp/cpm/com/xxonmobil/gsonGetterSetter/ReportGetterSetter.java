package jp.cpm.com.xxonmobil.gsonGetterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReportGetterSetter {
    @SerializedName("BeatWise_Report")
    @Expose
    private List<BeatWiseReport> beatWiseReport = null;

    public List<BeatWiseReport> getBeatWiseReport() {
        return beatWiseReport;
    }

    public void setBeatWiseReport(List<BeatWiseReport> beatWiseReport) {
        this.beatWiseReport = beatWiseReport;
    }

    @SerializedName("DSRwise_Report")
    @Expose
    private List<DSRwiseReport> dSRwiseReport = null;

    public List<DSRwiseReport> getDSRwiseReport() {
        return dSRwiseReport;
    }

    public void setDSRwiseReport(List<DSRwiseReport> dSRwiseReport) {
        this.dSRwiseReport = dSRwiseReport;
    }
}
