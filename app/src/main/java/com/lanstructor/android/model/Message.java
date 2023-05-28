package com.lanstructor.android.model;

public class Message {
    public String id;
    public String message;
    public String senderId;
    public Message(){}
    public Message(String id, String message, String senderId) {
        this.id = id;
        this.message = message;
        this.senderId = senderId;
    }
}
