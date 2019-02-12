
package jp.cpm.com.xxonmobil.gsonGetterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NoticeBoard {

    @SerializedName("Notice_Board_Url")
    @Expose
    private String noticeBoardUrl;

    public String getNoticeBoardUrl() {
        return noticeBoardUrl;
    }

    public void setNoticeBoardUrl(String noticeBoardUrl) {
        this.noticeBoardUrl = noticeBoardUrl;
    }

}
