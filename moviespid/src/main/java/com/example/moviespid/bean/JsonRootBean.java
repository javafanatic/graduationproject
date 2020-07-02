
package com.example.moviespid.bean;
import java.util.List;


public class JsonRootBean {

    private int count;
    private int start;
    private int total;
    private List<Subjects> subjects;
    private String title;
    public void setCount(int count) {
         this.count = count;
     }
     public int getCount() {
         return count;
     }

    public void setStart(int start) {
         this.start = start;
     }
     public int getStart() {
         return start;
     }

    public void setTotal(int total) {
         this.total = total;
     }
     public int getTotal() {
         return total;
     }

    public void setSubjects(List<Subjects> subjects) {
         this.subjects = subjects;
     }
     public List<Subjects> getSubjects() {
         return subjects;
     }

    public void setTitle(String title) {
         this.title = title;
     }
     public String getTitle() {
         return title;
     }

}