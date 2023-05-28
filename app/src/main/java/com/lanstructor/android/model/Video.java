package com.lanstructor.android.model;

import java.io.Serializable;

public class Video implements Serializable {
    public String id;
    public String title;
    public String urlPath;
    public String courseId;
    public Video(){}
    public Video(String id, String title, String urlPath,String courseId) {
        this.id = id;
        this.title = title;
        this.urlPath = urlPath;
        this.courseId = courseId;
    }
}
