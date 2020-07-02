package com.miaozhen.apiserver.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

//电影类
@Document(collection = "Movie")
public class Movie {
    @Id
    private String _id;

    private int mid;

    private String name;

    private String video;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Movie(String _id, int mid, String name, String video, String spic, String mpic, String lpic, String year, String actors, String country, String summary, String language, String directors, String writers) {
        this._id = _id;
        this.mid = mid;
        this.name = name;
        this.video = video;
        this.spic = spic;
        this.mpic = mpic;
        this.lpic = lpic;
        this.year = year;
        this.actors = actors;
        this.country = country;
        this.summary = summary;
        this.language = language;
        this.directors = directors;
        this.writers = writers;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getSpic() {
        return spic;
    }

    public void setSpic(String spic) {
        this.spic = spic;
    }

    public String getMpic() {
        return mpic;
    }

    public void setMpic(String mpic) {
        this.mpic = mpic;
    }

    public String getLpic() {
        return lpic;
    }

    public void setLpic(String lpic) {
        this.lpic = lpic;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDirectors() {
        return directors;
    }

    public void setDirectors(String directors) {
        this.directors = directors;
    }

    public String getWriters() {
        return writers;
    }

    public void setWriters(String writers) {
        this.writers = writers;
    }

    private String spic;

    private String mpic;

    private String lpic;

    private String year;

    private String actors;

    private String country;

    private String summary;

    private String language;

    private String directors;

    private String writers;


}
