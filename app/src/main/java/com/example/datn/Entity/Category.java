package com.example.datn.Entity;

import android.net.Uri;

import java.io.Serializable;

public class Category implements Serializable {
    private String id;
    private String picUrl;
    private String title;

    public Category() {
    }

    public Category(String id, String picUrl, String title) {
        this.id = id;
        this.picUrl = picUrl;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
