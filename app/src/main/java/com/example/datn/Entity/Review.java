package com.example.datn.Entity;

import java.io.Serializable;

public class Review implements Serializable {
    private String userId;
    private float rating;
    private String comment;

    public Review() {
    }

    public Review(String userId, float rating, String comment) {
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

