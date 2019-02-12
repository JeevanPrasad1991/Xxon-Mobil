
package jp.cpm.com.xxonmobil.gsonGetterSetter;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JourneyPlanSearchGetterSetter {

    @SerializedName("Journey_Plan_Search")
    @Expose
    private List<JourneyPlanSearch> journeyPlanSearch = null;

    public List<JourneyPlanSearch> getJourneyPlanSearch() {
        return journeyPlanSearch;
    }

    public void setJourneyPlanSearch(List<JourneyPlanSearch> journeyPlanSearch) {
        this.journeyPlanSearch = journeyPlanSearch;
    }

}
