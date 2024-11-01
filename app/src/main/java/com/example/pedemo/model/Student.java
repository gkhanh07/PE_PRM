package com.example.pedemo.model;

import java.util.Date;

public class Student {
    private int ID;
    private String name;
    private String date;
    private String gender;
    private String email;
    private String address;
    String majorId;
    private Major major;

    
    public Student() {
    }

    public Student(int ID, String name, String date,String gender, String email, String address, String majorId) {
        this.ID = ID;
        this.name = name;
        this.date=date;
        this.gender = gender;
        this.email = email;
        this.address = address;
        this.majorId = majorId;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMajorId() {
        return majorId;
    }

    public void setMajorId(String majorId) {
        this.majorId = majorId;
    }

    public Major getMajor() {
        return major;
    }

    public void setMajor(Major major) {
        this.major = major;
    }
}
