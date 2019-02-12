package jp.cpm.com.xxonmobil.gettersetter;

import java.io.Serializable;

/**
 * Created by upendrak on 19-12-2017.
 */

public class TrainingGetterSetter implements Serializable {

    private String rspname;
    private String trainingtype;
    private String topic;
    String key_id;
    private String photo;
    private String topic_cd;
    private String trainingtype_cd;
    private String rspname_cd;

    public boolean isFilter() {
        return filter;
    }

    public void setFilter(boolean filter) {
        this.filter = filter;
    }

    boolean filter=true;

    public String getUnoque_RSPID() {
        return unoque_RSPID;
    }

    public void setUnoque_RSPID(String unoque_RSPID) {
        this.unoque_RSPID = unoque_RSPID;
    }

    private String unoque_RSPID;


    public String getKey_id() {
        return key_id;
    }

    public void setKey_id(String key_id) {
        this.key_id = key_id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }


    public String getTopic_cd() {
        return topic_cd;
    }

    public void setTopic_cd(String topic_cd) {
        this.topic_cd = topic_cd;
    }

    public String getTrainingtype_cd() {
        return trainingtype_cd;
    }

    public void setTrainingtype_cd(String trainingtype_cd) {
        this.trainingtype_cd = trainingtype_cd;
    }

    public String getRspname_cd() {
        return rspname_cd;
    }

    public void setRspname_cd(String rspname_cd) {
        this.rspname_cd = rspname_cd;
    }

    public String getRspname() {
        return rspname;
    }

    public void setRspname(String rspname) {
        this.rspname = rspname;
    }

    public String getTrainingtype() {
        return trainingtype;
    }

    public void setTrainingtype(String trainingtype) {
        this.trainingtype = trainingtype;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TrainingGetterSetter)) {
            return false;
        }

        TrainingGetterSetter other = (TrainingGetterSetter) obj;
        return this.getTopic_cd().equals(other.getTopic_cd());
    }
    @Override
    public int hashCode() {
        // return super.hashCode();
        return getTopic_cd().hashCode();
    }


}




