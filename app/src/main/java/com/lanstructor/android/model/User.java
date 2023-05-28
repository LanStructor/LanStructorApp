package com.lanstructor.android.model;

import java.io.Serializable;

public class User implements Serializable {
    public String id;
    public String username;
    public String email;
    public String phone;
    public String mainLang;
    public String secondLang;
    public String userType;
    public String status;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String id, String username, String email, String phone, String mainLang, String secondLang, String userType,String status) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.mainLang = mainLang;
        this.secondLang = secondLang;
        this.userType = userType;
        this.status = status;
    }

    public User(String id, String username, String email, String phone, String mainLang, String secondLang, String userType) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.mainLang = mainLang;
        this.secondLang = secondLang;
        this.userType = userType;
    }
}
