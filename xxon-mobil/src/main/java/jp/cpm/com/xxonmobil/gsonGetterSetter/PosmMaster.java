
package jp.cpm.com.xxonmobil.gsonGetterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PosmMaster {

    @SerializedName("Posm_Id")
    @Expose
    private Integer posmId;
    @SerializedName("Posm")
    @Expose
    private String posm;
    @SerializedName("Posm_Type_Id")
    @Expose
    private Integer posmTypeId;
    @SerializedName("Posm_Type")
    @Expose
    private String posmType;

    @SerializedName("Ref_Image")
    @Expose
    private String refImage;

    public Integer getPosmId() {
        return posmId;
    }

    public void setPosmId(Integer posmId) {
        this.posmId = posmId;
    }

    public String getPosm() {
        return posm;
    }

    public void setPosm(String posm) {
        this.posm = posm;
    }

    public Integer getPosmTypeId() {
        return posmTypeId;
    }

    public void setPosmTypeId(Integer posmTypeId) {
        this.posmTypeId = posmTypeId;
    }

    public String getPosmType() {
        return posmType;
    }

    public void setPosmType(String posmType) {
        this.posmType = posmType;
    }

    String posm_deployment = "";
    String deployment_img_one = "";
    String currect_reason_Id = "0";

    public String getPosm_deployment() {
        return posm_deployment;
    }

    public void setPosm_deployment(String posm_deployment) {
        this.posm_deployment = posm_deployment;
    }

    public String getDeployment_img_one() {
        return deployment_img_one;
    }

    public void setDeployment_img_one(String deployment_img_one) {
        this.deployment_img_one = deployment_img_one;
    }

    public String getCurrect_reason_Id() {
        return currect_reason_Id;
    }

    public void setCurrect_reason_Id(String currect_reason_Id) {
        this.currect_reason_Id = currect_reason_Id;
    }

    public String getCurrect_reason() {
        return currect_reason;
    }

    public void setCurrect_reason(String currect_reason) {
        this.currect_reason = currect_reason;
    }

    String currect_reason = "";

    public String getRefImage() {
        return refImage;
    }

    public void setRefImage(String refImage) {
        this.refImage = refImage;
    }


    String checklist_Id;
    String checklist;
    String ans_Id;
    String ans;
    String currectans_Ic = "0";

    public String getCurrectans() {
        return currectans;
    }

    public void setCurrectans(String currectans) {
        this.currectans = currectans;
    }

    String currectans="";

    public String getChecklist_Id() {
        return checklist_Id;
    }

    public void setChecklist_Id(String checklist_Id) {
        this.checklist_Id = checklist_Id;
    }

    public String getChecklist() {
        return checklist;
    }

    public void setChecklist(String checklist) {
        this.checklist = checklist;
    }

    public String getAns_Id() {
        return ans_Id;
    }

    public void setAns_Id(String ans_Id) {
        this.ans_Id = ans_Id;
    }

    public String getAns() {
        return ans;
    }

    public void setAns(String ans) {
        this.ans = ans;
    }

    public String getCurrectans_Ic() {
        return currectans_Ic;
    }

    public void setCurrectans_Ic(String currectans_Ic) {
        this.currectans_Ic = currectans_Ic;
    }

    public String getDeployment_img_two() {
        return deployment_img_two;
    }

    public void setDeployment_img_two(String deployment_img_two) {
        this.deployment_img_two = deployment_img_two;
    }

    String deployment_img_two = "";

    public String getKey_Id() {
        return key_Id;
    }

    public void setKey_Id(String key_Id) {
        this.key_Id = key_Id;
    }

    String key_Id;

    public boolean isRemarkflag() {
        return remarkflag;
    }

    public void setRemarkflag(boolean remarkflag) {
        this.remarkflag = remarkflag;
    }

    boolean remarkflag=false;

    public String getEdittext_remarkfor_others() {
        return edittext_remarkfor_others;
    }

    public void setEdittext_remarkfor_others(String edittext_remarkfor_others) {
        this.edittext_remarkfor_others = edittext_remarkfor_others;
    }

    String edittext_remarkfor_others="";
}
