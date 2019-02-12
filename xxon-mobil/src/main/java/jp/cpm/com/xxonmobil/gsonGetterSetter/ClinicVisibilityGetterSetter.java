package jp.cpm.com.xxonmobil.gsonGetterSetter;

/**
 * Created by jeevanp on 5/31/2018.
 */

public class ClinicVisibilityGetterSetter {
    public int getVisibilityCd() {
        return visibilityCd;
    }

    public void setVisibilityCd(int visibilityCd) {
        this.visibilityCd = visibilityCd;
    }

    int visibilityCd;
    String visibilityNM="";


    public String getVisibilityNM() {
        return visibilityNM;
    }

    public void setVisibilityNM(String visibilityNM) {
        this.visibilityNM = visibilityNM;
    }



    public String getVisibility_IMG() {
        return visibility_IMG;
    }

    public void setVisibility_IMG(String visibility_IMG) {
        this.visibility_IMG = visibility_IMG;
    }


    public String getVisibilityPosmQuantity() {
        return visibilityPosmQuantity;
    }

    public void setVisibilityPosmQuantity(String visibilityPosmQuantity) {
        this.visibilityPosmQuantity = visibilityPosmQuantity;
    }

    String visibilityPosmQuantity="";
    String visibility_IMG="";
}
