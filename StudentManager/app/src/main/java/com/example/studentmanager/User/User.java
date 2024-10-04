package com.example.studentmanager.User;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String born;
    private String sex;
    private String address;
    private String position;
    private String phone_number;
    private String email;
    private String permanent_address;
    private String image;
    private String employ_code;

    public User() {
    }

    public User(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public User(String name, String born, String sex, String address, String position, String phone_number, String email, String permanent_address, String image, String employ_code) {
        this.name = name;
        this.born = born;
        this.sex = sex;
        this.address = address;
        this.position = position;
        this.phone_number = phone_number;
        this.email = email;
        this.permanent_address = permanent_address;
        this.image = image;
        this.employ_code = employ_code;
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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPermanent_address() {
        return permanent_address;
    }

    public void setPermanent_address(String permanent_address) {
        this.permanent_address = permanent_address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmploy_code() {
        return employ_code;
    }

    public void setEmploy_code(String employ_code) {
        this.employ_code = employ_code;
    }
}
