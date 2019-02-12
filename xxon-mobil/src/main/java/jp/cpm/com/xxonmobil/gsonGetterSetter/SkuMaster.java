
package jp.cpm.com.xxonmobil.gsonGetterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SkuMaster {

    @SerializedName("Sku_Id")
    @Expose
    private Integer skuId;
    @SerializedName("Sku")
    @Expose
    private String sku;
    @SerializedName("Brand_Id")
    @Expose
    private Integer brandId;
    @SerializedName("Sku_Sequence")
    @Expose
    private Integer skuSequence;

    public String getSAMPLING_QTY() {
        return SAMPLING_QTY;
    }

    public void setSAMPLING_QTY(String SAMPLING_QTY) {
        this.SAMPLING_QTY = SAMPLING_QTY;
    }

    private String SAMPLING_QTY="";




    public String getKey_id() {
        return key_id;
    }

    public void setKey_id(String key_id) {
        this.key_id = key_id;
    }
    String key_id;
    public Integer getSkuId() {
        return skuId;
    }
    public void setSkuId(Integer skuId) {
        this.skuId = skuId;
    }
    public String getSku() {
        return sku;
    }
    public void setSku(String sku) {
        this.sku = sku;
    }
    public Integer getBrandId() {
        return brandId;
    }
    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }
    public Integer getSkuSequence() {
        return skuSequence;
    }
    public void setSkuSequence(Integer skuSequence) {
        this.skuSequence = skuSequence;
    }

}
