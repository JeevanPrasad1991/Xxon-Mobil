package jp.cpm.com.xxonmobil.gsonGetterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AllObjectGetterSetter {

    @SerializedName("Mapping_User_City")
    @Expose
    private List<MappingUserCity> mappingUserCity = null;

    public List<MappingUserCity> getMappingUserCity() {
        return mappingUserCity;
    }

    @SerializedName("Journey_Plan_Audit")
    @Expose
    private List<JourneyPlan> journeyPlan = null;
    public List<JourneyPlan> getJourneyPlan() {
        return journeyPlan;
    }

}
