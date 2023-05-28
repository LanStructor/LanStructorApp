package com.lanstructor.android.model;

import java.io.Serializable;

public class Appointment implements Serializable {
    public String id;
    public String time;
    public String date;
    public String studentId;
    public String instructorId;
    public String status;
    public Appointment(){}

    public Appointment(String id, String time, String date, String studentId, String instructorId, String status) {
        this.id = id;
        this.time = time;
        this.date = date;
        this.studentId = studentId;
        this.instructorId = instructorId;
        this.status = status;
    }
}
