package com.example.studentmanager.Schuele;

public class Schedule {
    private String title;
    private String address;
    private String time;

    public Schedule() {
    }

    public Schedule(String title, String address, String time) {
        this.title = title;
        this.address = address;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
