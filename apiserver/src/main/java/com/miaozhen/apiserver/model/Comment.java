package com.miaozhen.apiserver.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Comment")
public class Comment {
    @Id
    private String _id;

    private int uid;

    private int mid;

    private double score;

    private long timestamp;
    private String comContents;

    public String getComContents() {
        return comContents;
    }

    public void setComContents(String comContents) {
        this.comContents = comContents;
    }

    public Comment(int uid, int mid, double score, long timestamp) {
        this.uid = uid;
        this.mid = mid;
        this.score = score;
        this.timestamp = timestamp;
    }

    public String  get_id() {
        return _id;
    }

    public void set_id(String  _id) {
        this._id = _id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
