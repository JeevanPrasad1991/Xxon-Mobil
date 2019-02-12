package jp.cpm.com.xxonmobil.gsonGetterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DSRList {
    @SerializedName("DSR_Code")
    @Expose
    private String dSRCode;
    @SerializedName("Preferred")
    @Expose
    private String preferred;

    public String getDSRCode() {
        return dSRCode;
    }

    public void setDSRCode(String dSRCode) {
        this.dSRCode = dSRCode;
    }

    public String getPreferred() {
        return preferred;
    }

    public void setPreferred(String preferred) {
        this.preferred = preferred;
    }
}
