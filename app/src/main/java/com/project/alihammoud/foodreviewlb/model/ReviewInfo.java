package com.project.alihammoud.foodreviewlb.model;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class ReviewInfo {


    private String restaurant_name;
    private String user_id;
    private String description;
    private @ServerTimestamp Date date_posted;
    private float rating;
    private String review_image;


   public ReviewInfo(){

    }

    public ReviewInfo(String user_id, String restaurant_name, String description, Date date_posted, float rating, String review_image) {
       this.user_id = user_id;
       this.restaurant_name = restaurant_name;
       this.description = description;
       this.date_posted = date_posted;
       this.rating = rating;
       this.review_image = review_image;

    }

    public String getRestaurant_name() {
        return restaurant_name;
    }

    public void setRestaurant_name(String restaurant_name) {
        this.restaurant_name = restaurant_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate_posted() {
        return date_posted;
    }

    public void setDate_posted(Date date_posted) {
        this.date_posted = date_posted;
    }


    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
    public String getReview_image() {
        return review_image;
    }

    public void setReview_image(String review_image) {
        this.review_image = review_image;
    }




}
