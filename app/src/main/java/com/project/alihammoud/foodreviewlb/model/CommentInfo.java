package com.project.alihammoud.foodreviewlb.model;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class CommentInfo {

    private String user_id;
    private String comment;
    private @ServerTimestamp Date date_posted;


    public CommentInfo(){

    }

    public CommentInfo(String user_id, String comment, Date date_posted) {

        this.user_id = user_id;
        this.comment = comment;
        this.date_posted = date_posted;

    }


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDate_posted() {
        return date_posted;
    }

    public void setDate_posted(Date date_posted) {
        this.date_posted = date_posted;
    }



}
