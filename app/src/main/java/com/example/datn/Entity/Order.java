package com.example.datn.Entity;

import java.util.ArrayList;

public class Order {
    private String orderId;
    private String userId;
    private String name;
    private String phone;
    private String address;
    private String orderDate;
    private ArrayList<CartItem> products;  // Giả sử bạn sử dụng CartItem cho sản phẩm trong đơn hàng
    private double totalAmount;
    private String status;
    private String voucherApplied;

    public Order() {
        // Constructor không đối số cho Firebase
    }

    public Order(String orderId, String userId, String name, String phone, String address, String orderDate, ArrayList<CartItem> products, double totalAmount, String status, String voucherApplied) {
        this.orderId = orderId;
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.orderDate = orderDate;
        this.products = products;
        this.totalAmount = totalAmount;
        this.status = status;
        this.voucherApplied = voucherApplied;
    }

    public Order(String orderId, String userId, String name, String phone, String address, String orderDate, ArrayList<CartItem> products, String status) {
        this.orderId = orderId;
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.orderDate = orderDate;
        this.products = products;
        this.totalAmount = findTotalAmount(products);
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public ArrayList<CartItem> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<CartItem> products) {
        this.products = products;
    }

    public double getTotalAmount() {
        return totalAmount;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVoucherApplied() {
        return voucherApplied;
    }

    public void setVoucherApplied(String voucherApplied) {
        this.voucherApplied = voucherApplied;
    }

    public float findTotalAmount(ArrayList<CartItem> products){
        float totalAmount = 0.0f;
        for (CartItem product : products){
            totalAmount += product.getQuantity() * product.getPrice();
        }
        return totalAmount;
    }
}
