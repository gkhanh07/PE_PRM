package com.example.pedemo.model;

public class Major {
    private String IDMajor;
    private String nameMajor;

    public Major() {
    }

    public Major(String IDMajor, String nameMajor) {
        this.IDMajor = IDMajor;
        this.nameMajor = nameMajor;
    }

    public String getIDMajor() {
        return IDMajor;
    }

    public void setIDMajor(String IDMajor) {
        this.IDMajor = IDMajor;
    }

    public String getNameMajor() {
        return nameMajor;
    }

    public void setNameMajor(String nameMajor) {
        this.nameMajor = nameMajor;
    }

    @Override
    public String toString() {
        return nameMajor; // Trả về tên major để hiển thị trong spinner
    }
}
