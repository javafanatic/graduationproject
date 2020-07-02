
package com.example.moviespid.CommentBean;
import java.util.List;


public class RootBean {

    private int count;
    private List<Comments> comments;
    private int start;
    private long total;
    private int next_start;
    private Subject subject;
    public void setCount(int count) {
         this.count = count;
     }
     public int getCount() {
         return count;
     }

    public void setComments(List<Comments> comments) {
         this.comments = comments;
     }
     public List<Comments> getComments() {
         return comments;
     }

    public void setStart(int start) {
         this.start = start;
     }
     public int getStart() {
         return start;
     }

    public void setTotal(long total) {
         this.total = total;
     }
     public long getTotal() {
         return total;
     }

    public void setNext_start(int next_start) {
         this.next_start = next_start;
     }
     public int getNext_start() {
         return next_start;
     }

    public void setSubject(Subject subject) {
         this.subject = subject;
     }
     public Subject getSubject() {
         return subject;
     }

}