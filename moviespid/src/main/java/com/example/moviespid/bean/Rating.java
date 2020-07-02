
package com.example.moviespid.bean;

public class Rating {

    private int max;
    private double average;
    private String datails;
    private String stars;
    private int min;
    public void setMax(int max) {
         this.max = max;
     }
     public int getMax() {
         return max;
     }

    public void setAverage(double average) {
         this.average = average;
     }
     public double getAverage() {
         return average;
     }


    public String getDatails() {
        return datails;
    }

    public void setDatails(String datails) {
        this.datails = datails;
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