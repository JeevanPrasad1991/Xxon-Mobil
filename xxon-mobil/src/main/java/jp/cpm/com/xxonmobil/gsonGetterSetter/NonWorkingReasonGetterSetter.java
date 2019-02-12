
package jp.cpm.com.xxonmobil.gsonGetterSetter;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NonWorkingReasonGetterSetter {

    @SerializedName("Non_Working_Reason")
    @Expose
    private List<NonWorkingReason> nonWorkingReason = null;

    public List<NonWorkingReason> getNonWorkingReason() {
        return nonWorkingReason;
    }

    public void setNonWorkingReason(List<NonWorkingReason> nonWorkingReason) {
        this.nonWorkingReason = nonWorkingReason;
    }

}
