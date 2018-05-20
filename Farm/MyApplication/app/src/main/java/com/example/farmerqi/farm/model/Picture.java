package com.example.farmerqi.farm.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by FarmerQi on 2018/2/20.
 */
public class Picture {
    private int picID;
    private int productID;
    private String name;
    private String location;
    private Timestamp createdTime;

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getPicID() {
        return picID;
    }

    public void setPicID(int picID) {
        this.picID = picID;
    }
}
