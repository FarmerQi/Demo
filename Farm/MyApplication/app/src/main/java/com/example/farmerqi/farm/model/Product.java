package com.example.farmerqi.farm.model;

import java.sql.Timestamp;
import java.util.List;

public class Product {
    private int productID;
    private int userID;
    //商品名
    private String productName;
    //商品价格
    private String productPrice;



    //商品数量
    private String productAmount;
    private String tag;
    private Timestamp productCreatedTime;

    private User user;
    private Location location;
    private List<Picture> pictures;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductAmount() {
        return productAmount;
    }

    public void setProductAmount(String productAmount) {
        this.productAmount = productAmount;
    }

    public Timestamp getProductCreatedTime() {
        return productCreatedTime;
    }

    public void setProductCreatedTime(Timestamp productCreatedTime) {
        this.productCreatedTime = productCreatedTime;
    }
}
