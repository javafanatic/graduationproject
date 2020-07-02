package com.miaozhen.apiserver.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "UserRecs")
public class UserRec {
    @Id
    private String _id;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    private int uid;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public List<UserRecommend> getRecs() {
        return recs;
    }

    public void setRecs(List<UserRecommend> recs) {
        this.recs = recs;
    }

    private List<UserRecommend> recs;
}
