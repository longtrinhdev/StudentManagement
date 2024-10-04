package com.example.studentmanager.Students;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Student implements Serializable{
    private String name;
    private String born;
    private String image;
    private String address;
    private String phone_number;
    private String sex;
    private String student_code;
    private Score score;

    public Student() {}

    public Student(String name, String born, String image, String address, String phone_number, String sex, String student_code, Score score) {
        this.name = name;
        this.born = born;
        this.image = image;
        this.address = address;
        this.phone_number = phone_number;
        this.sex = sex;
        this.student_code = student_code;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBorn() {
        return born;
    }

    public void setBorn(String born) {
        this.born = born;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getStudent_code() {
        return student_code;
    }

    public void setStudent_code(String student_code) {
        this.student_code = student_code;
    }

    public Score getScore() {
        return score;
    }

    public void setScore(Score score) {
        this.score = score;
    }

    public double getAverageScore() {
        double total = this.score.getMath() + this.score.getLiterature() + this.score.getForeign_language();
        return total / 3;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("score", getScore().mapScore());
        return  map;
    }
}
