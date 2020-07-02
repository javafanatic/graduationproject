package com.miaozhen.apiserver.model;

public class RecentRate implements  Comparable<RecentRate>{
    private int mid;
    private double score;

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

    public RecentRate(int mid, double score) {
        this.mid = mid;
        this.score = score;
    }

    public RecentRate() {
    }

    @Override
    public int compareTo(RecentRate o) {
        if(this.score<o.score){
            return 1;
        }
        else {
            return -1;
        }
    }
}
