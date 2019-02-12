package jp.cpm.com.xxonmobil.gsonGetterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LastVisitPosmExecution {
    @SerializedName("Store_Id")
    @Expose
    private Integer storeId;
    @SerializedName("MID")
    @Expose
    private Integer mID;
    @SerializedName("Posm_Id")
    @Expose
    private Integer posmId;
    @SerializedName("Installed")
    @Expose
    private Boolean installed;
    @SerializedName("Preason_Id")
    @Expose
    private Integer preasonId;
    @SerializedName("Image_Url")
    @Expose
    private String imageUrl;
    @SerializedName("Image_Path")
    @Expose
    private String imagePath;


    @SerializedName("Remark")
    @Expose
    private String remark;

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getMID() {
        return mID;
    }

    public void setMID(Integer mID) {
        this.mID = mID;
    }

    public Integer getPosmId() {
        return posmId;
    }

    public void setPosmId(Integer posmId) {
        this.posmId = posmId;
    }

    public Boolean getInstalled() {
        return installed;
    }

    public void setInstalled(Boolean installed) {
        this.installed = installed;
    }

    public Integer getPreasonId() {
        return preasonId;
    }

    public void setPreasonId(Integer preasonId) {
        this.preasonId = preasonId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
