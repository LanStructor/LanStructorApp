package com.lanstructor.android.model;

import java.io.Serializable;

public class Course implements Serializable {
    public String id;
    public String instId;
    public String name;
    public String details;
    public String price;
    public String lang;
    public String category;

    public Course(){}

    public Course(String id, String instId, String name, String details, String price, String lang, String category) {
        this.id = id;
        this.instId = instId;
        this.name = name;
        this.details = details;
        this.price = price;
        this.lang = lang;
        this.category = category;
    }
}
