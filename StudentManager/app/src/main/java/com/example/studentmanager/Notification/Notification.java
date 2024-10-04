package com.example.studentmanager.Notification;

import java.io.Serializable;

public class Notification implements Serializable {
    private String title;
    private String time;
    private String link;

    public Notification() {
    }

    public Notification(String title, String time, String link) {
        this.title = title;
        this.time = time;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
