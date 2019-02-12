
package jp.cpm.com.xxonmobil.gsonGetterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuditQuestion {
    @SerializedName("Question_Id")
    @Expose
    private Integer questionId;
    @SerializedName("Question")
    @Expose
    private String question;
    @SerializedName("Answer_Id")
    @Expose
    private Integer answerId;
    @SerializedName("Answer")
    @Expose
    private String answer;
    @SerializedName("Question_Category_Id")
    @Expose
    private Integer questionCategoryId;
    @SerializedName("Question_Category")
    @Expose
    private String questionCategory;

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Integer getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Integer answerId) {
        this.answerId = answerId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Integer getQuestionCategoryId() {
        return questionCategoryId;
    }

    public void setQuestionCategoryId(Integer questionCategoryId) {
        this.questionCategoryId = questionCategoryId;
    }

    public String getQuestionCategory() {
        return questionCategory;
    }

    public void setQuestionCategory(String questionCategory) {
        this.questionCategory = questionCategory;
    }



    public String getCurrectanswerCd() {
        return currectanswerCd;
    }

    public String getCameraFLAG() {
        return cameraFLAG;
    }

    public void setCameraFLAG(String cameraFLAG) {
        this.cameraFLAG = cameraFLAG;
    }

    public String cameraFLAG;

    public void setCurrectanswerCd(String currectanswerCd) {
        this.currectanswerCd = currectanswerCd;
    }


    public String getCurrectanswer() {
        return currectanswer;
    }

    public void setCurrectanswer(String currectanswer) {
        this.currectanswer = currectanswer;
    }
    public String currectanswerCd;
    public String currectanswer;

    public String getAudit_cam() {
        return audit_cam;
    }
    @SerializedName("Image_Allow")
    @Expose
    private String  imageAllow;

    public String getImageAllowforanswer() {
        return imageAllowforanswer;
    }

    public void setImageAllowforanswer(String imageAllowforanswer) {
        this.imageAllowforanswer = imageAllowforanswer;
    }

    @SerializedName("ImageAllow")
    @Expose
    private String  imageAllowforanswer;

    public String getRemark_allow() {
        return remark_allow;
    }

    public void setRemark_allow(String remark_allow) {
        this.remark_allow = remark_allow;
    }

    @SerializedName("Remark")
    @Expose
    private String  remark_allow;


    public void setAudit_cam(String audit_cam) {
        this.audit_cam = audit_cam;
    }

    public String audit_cam;

    public String  getImageAllow() {
        return imageAllow;
    }

    public void setImageAllow(String imageAllow) {
        this.imageAllow = imageAllow;
    }

    String answer_remark="";

    public String getAnswer_remark() {
        return answer_remark;
    }

    public void setAnswer_remark(String answer_remark) {
        this.answer_remark = answer_remark;
    }

    public String getAnsremark_allow() {
        return ansremark_allow;
    }

    public void setAnsremark_allow(String ansremark_allow) {
        this.ansremark_allow = ansremark_allow;
    }

    String ansremark_allow="";

}
