package com.example.datn.Entity;

public class CartItem {
    private String cartId;
    private String productId;
    private String productName;
    private double price;
    private String size;
    private String color;
    private int quantity;

    // Constructor không tham số
    public CartItem() {
    }

    public CartItem(String cartId, String productId, String productName, double price, String size, String color, int quantity) {
        this.cartId = cartId;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.size = size;
        this.color = color;
        this.quantity = quantity;
    }


    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    // Getter và Setter cho productId
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    // Getter và Setter cho productName
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    // Getter và Setter cho price
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // Getter và Setter cho quantity
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
