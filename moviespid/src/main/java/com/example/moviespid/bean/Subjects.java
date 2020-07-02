
package com.example.moviespid.bean;
import java.util.List;
import java.util.Date;


public class Subjects {

    private Rating rating;
    private List<String> genres;
    private String title;
    private List<Casts> casts;
    private List<String> durations;
    private int collect_count;
    private String mainland_pubdate;
    private boolean has_video;
    private String original_title;
    private String subtype;
    private List<Directors> directors;
    private List<String> pubdates;
    private String year;
    private Images images;
    private String alt;
    private String id;
    public void setRating(Rating rating) {
         this.rating = rating;
     }
     public Rating getRating() {
         return rating;
     }

    public void setGenres(List<String> genres) {
         this.genres = genres;
     }
     public List<String> getGenres() {
         return genres;
     }

    public void setTitle(String title) {
         this.title = title;
     }
     public String getTitle() {
         return title;
     }

    public void setCasts(List<Casts> casts) {
         this.casts = casts;
     }
     public List<Casts> getCasts() {
         return casts;
     }

    public void setDurations(List<String> durations) {
         this.durations = durations;
     }
     public List<String> getDurations() {
         return durations;
     }

    public void setCollect_count(int collect_count) {
         this.collect_count = collect_count;
     }
     public int getCollect_count() {
         return collect_count;
     }

    public String getMainland_pubdate() {
        return mainland_pubdate;
    }

    public void setMainland_pubdate(String mainland_pubdate) {
        this.mainland_pubdate = mainland_pubdate;
    }

    public boolean isHas_video() {
        return has_video;
    }

    public void setHas_video(boolean has_video) {
         this.has_video = has_video;
     }
     public boolean getHas_video() {
         return has_video;
     }

    public void setOriginal_title(String original_title) {
         this.original_title = original_title;
     }
     public String getOriginal_title() {
         return original_title;
     }

    public void setSubtype(String subtype) {
         this.subtype = subtype;
     }
     public String getSubtype() {
         return subtype;
     }

    public void setDirectors(List<Directors> directors) {
         this.directors = directors;
     }
     public List<Directors> getDirectors() {
         return directors;
     }

    public List<String> getPubdates() {
        return pubdates;
    }

    public void setPubdates(List<String> pubdates) {
        this.pubdates = pubdates;
    }

    public void setYear(String year) {
         this.year = year;
     }
     public String getYear() {
         return year;
     }

    public void setImages(Images images) {
         this.images = images;
     }
     public Images getImages() {
         return images;
     }

    public void setAlt(String alt) {
         this.alt = alt;
     }
     public String getAlt() {
         return alt;
     }

    public void setId(String id) {
         this.id = id;
     }
     public String getId() {
         return id;
     }

}