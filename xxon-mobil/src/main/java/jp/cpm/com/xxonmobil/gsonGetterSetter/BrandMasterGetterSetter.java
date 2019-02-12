
package jp.cpm.com.xxonmobil.gsonGetterSetter;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BrandMasterGetterSetter {

    @SerializedName("Brand_Master")
    @Expose
    private List<BrandMaster> brandMaster = null;

    public List<BrandMaster> getBrandMaster() {
        return brandMaster;
    }

    public void setBrandMaster(List<BrandMaster> brandMaster) {
        this.brandMaster = brandMaster;
    }

}
