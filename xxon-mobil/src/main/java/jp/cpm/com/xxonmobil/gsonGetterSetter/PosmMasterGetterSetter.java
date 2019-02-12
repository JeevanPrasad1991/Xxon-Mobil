
package jp.cpm.com.xxonmobil.gsonGetterSetter;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PosmMasterGetterSetter {

    @SerializedName("Posm_Master")
    @Expose
    private List<PosmMaster> posmMaster = null;

    public List<PosmMaster> getPosmMaster() {
        return posmMaster;
    }

    public void setPosmMaster(List<PosmMaster> posmMaster) {
        this.posmMaster = posmMaster;
    }

    @SerializedName("Non_Posm_Reason")
    @Expose
    private List<NonPosmReason> nonPosmReason = null;

    public List<NonPosmReason> getNonPosmReason() {
        return nonPosmReason;
    }


    @SerializedName("Posm_Checklist")
    @Expose
    private List<PosmChecklist> posmChecklist = null;

    public List<PosmChecklist> getPosmChecklist() {
        return posmChecklist;
    }

    @SerializedName("Mapping_Posm_Checklist")
    @Expose
    private List<MappingPosmChecklist> mappingPosmChecklist = null;

    public List<MappingPosmChecklist> getMappingPosmChecklist() {
        return mappingPosmChecklist;
    }

}
