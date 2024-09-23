package com.example.datn.Entity;

public class User {
    private String name;
    private String email;
    private String password;
    private String phone;
    private String address;
    private String imgProfile;
    private String role;

    // Constructor không tham số
    public User() {
    }

    // Constructor có tham số
    public User(String name, String email, String password, String phone, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = role;
    }

    public User(String name, String email, String password, String phone, String imgProfile, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.imgProfile = imgProfile;
        this.role = role;
    }

    public User(String name, String email, String password, String phone, String address, String imgProfile, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.imgProfile = imgProfile;
        this.role = role;
    }

    public User(String name, String phone, String address) {
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getImgProfile() {
        return imgProfile;
    }

    public void setImgProfile(String imgProfile) {
        this.imgProfile = imgProfile;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

