package com.hikari.spacecardscollection.RoomsImplementation.RoomsChat;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;


public class Message {

    private @ServerTimestamp Timestamp date;
    private String id;
    private String text;
    private String user;

    public Message(Timestamp date, String id, String text, String user) {
        this.date = date;
        this.id = id;
        this.text = text;
        this.user = user;
    }

    public Message() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
