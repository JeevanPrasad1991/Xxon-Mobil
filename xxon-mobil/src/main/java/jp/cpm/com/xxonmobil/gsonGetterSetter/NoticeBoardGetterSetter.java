
package jp.cpm.com.xxonmobil.gsonGetterSetter;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NoticeBoardGetterSetter {

    @SerializedName("Notice_Board")
    @Expose
    private List<NoticeBoard> noticeBoard = null;

    public List<NoticeBoard> getNoticeBoard() {
        return noticeBoard;
    }

    public void setNoticeBoard(List<NoticeBoard> noticeBoard) {
        this.noticeBoard = noticeBoard;
    }

}
