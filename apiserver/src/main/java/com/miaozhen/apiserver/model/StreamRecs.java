package com.miaozhen.apiserver.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
@Document("StreamRecs")
public class StreamRecs {
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

    private String recs;

    public String getRecs() {
        return recs;
    }

    public void setRecs(String recs) {
        this.recs = recs;
    }
}
