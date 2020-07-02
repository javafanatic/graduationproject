
package com.example.moviespid.CommentBean;


public class Rating {

    private int max;
    private double value;
    private String details;
    private String stars;
    private int min;
    public void setMax(int max) {
         this.max = max;
     }
     public int getMax() {
         return max;
     }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setStars(String stars) {
         this.stars = stars;
     }
     public String getStars() {
         return stars;
     }

    public void setMin(int min) {
         this.min = min;
     }
     public int getMin() {
         return min;
     }

}