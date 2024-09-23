package com.example.datn.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Product implements Serializable {

    private String productId;
    private String productName;
    private String categoryId;
    private int price;

    private ArrayList<String> sizes;

    private ArrayList<String> colors;
    private int stock;
    private String description;
    private ArrayList<String> picUrls;
    private ArrayList<Review> reviews;



    public Product() {
    }

    public Product(String productId, String productName, String categoryId, int price, int stock, String description, ArrayList<String> picUrls, ArrayList<Review> reviews, ArrayList<String> sizes, ArrayList<String> colors) {
        this.productId = productId;
        this.productName = productName;
        this.categoryId = categoryId;
        this.price = price;
        this.stock = stock;
        this.description = description;
        this.picUrls = picUrls;
        this.reviews = reviews;
        this.sizes = sizes;
        this.colors = colors;
    }

    public Product(String productId, String productName, String categoryId, int price, int stock, String description, ArrayList<String> picUrls) {
        this.productId = productId;
        this.productName = productName;
        this.categoryId = categoryId;
        this.price = price;
        this.stock = stock;
        this.description = description;
        this.picUrls = picUrls;
    }

    public Product(String productId, String productName, String categoryId, int price, ArrayList<String> sizes, int stock, String description, ArrayList<String> picUrls) {
        this.productId = productId;
        this.productName = productName;
        this.categoryId = categoryId;
        this.price = price;
        this.sizes = sizes;
        this.stock = stock;
        this.description = description;
        this.picUrls = picUrls;
    }

    public Product(String productId, String productName, String categoryId, int price, ArrayList<String> colors, int stock, String description, ArrayList<String> picUrls, ArrayList<Review> reviews) {
        this.productId = productId;
        this.productName = productName;
        this.categoryId = categoryId;
        this.price = price;
        this.colors = colors;
        this.stock = stock;
        this.description = description;
        this.picUrls = picUrls;
        this.reviews = reviews;
    }

    public Product(String productId, String productName, String categoryId, int price, ArrayList<String> sizes, ArrayList<String> colors, int stock, String description, ArrayList<String> picUrls) {
        this.productId = productId;
        this.productName = productName;
        this.categoryId = categoryId;
        this.price = price;
        this.sizes = sizes;
        this.colors = colors;
        this.stock = stock;
        this.description = description;
        this.picUrls = picUrls;
    }

    public Product(String productId, String productName, int price, int stock, String description, ArrayList<String> picUrls) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.stock = stock;
        this.description = description;
        this.picUrls = picUrls;
    }

    public float getAverageRating() {
        if (reviews == null || reviews.isEmpty()) {
            return 0;  // Trả về 0 nếu không có đánh giá nào
        }

        float totalRating = 0;
        for (Review review : reviews) {
            totalRating += review.getRating();  // Cộng điểm rating của từng đánh giá
        }

        // Tính trung bình
        return totalRating / reviews.size();
    }

    public int getReviewCount() {
        if (reviews == null || reviews.isEmpty()) {
            return 0;  // Trả về 0 nếu không có đánh giá nào
        }
        return reviews.size();  // Trả về số lượng đánh giá
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getPicUrls() {
        return picUrls;
    }

    public void setPicUrls(ArrayList<String> picUrls) {
        this.picUrls = picUrls;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    public ArrayList<String> getSizes() {
        return sizes;
    }

    public void setSizes(ArrayList<String> sizes) {
        this.sizes = sizes;
    }

    public ArrayList<String> getColors() {
        return colors;
    }

    public void setColors(ArrayList<String> colors) {
        this.colors = colors;
    }
}