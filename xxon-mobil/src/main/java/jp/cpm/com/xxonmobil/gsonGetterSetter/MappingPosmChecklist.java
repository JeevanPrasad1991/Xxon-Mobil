package jp.cpm.com.xxonmobil.gsonGetterSetter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MappingPosmChecklist {

    @SerializedName("Posm_Id")
    @Expose
    private Integer posmId;
    @SerializedName("Checklist_Id")
    @Expose
    private Integer checklistId;

    public Integer getPosmId() {
        return posmId;
    }

    public void setPosmId(Integer posmId) {
        this.posmId = posmId;
    }

    public Integer getChecklistId() {
        return checklistId;
    }

    public void setChecklistId(Integer checklistId) {
        this.checklistId = checklistId;
    }

}