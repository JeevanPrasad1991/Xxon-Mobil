
package jp.cpm.com.xxonmobil.gsonGetterSetter;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuditQuestionGetterSetter {

    @SerializedName("Audit_Question")
    @Expose
    private List<AuditQuestion> auditQuestion = null;

    public List<AuditQuestion> getAuditQuestion() {
        return auditQuestion;
    }

    public void setAuditQuestion(List<AuditQuestion> auditQuestion) {
        this.auditQuestion = auditQuestion;
    }

}
