
package jp.cpm.com.xxonmobil.gsonGetterSetter;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JCPGetterSetter {
    @SerializedName("Journey_Plan")
    @Expose
    private List<JourneyPlan> journeyPlan = null;
    public List<JourneyPlan> getJourneyPlan() {
        return journeyPlan;
    }

    @SerializedName("Mapping_Posm")
    @Expose
    private List<MappingPosm> mappingPosm = null;

    public List<MappingPosm> getMappingPosm() {
        return mappingPosm;
    }

    @SerializedName("Last_Visit_Posm_Execution")
    @Expose
    private List<LastVisitPosmExecution> lastVisitPosmExecution = null;

    public List<LastVisitPosmExecution> getLastVisitPosmExecution() {
        return lastVisitPosmExecution;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    String user_type;
}
