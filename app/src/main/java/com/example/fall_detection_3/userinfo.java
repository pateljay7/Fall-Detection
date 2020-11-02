package com.example.fall_detection_3;

public class userinfo {
    private String name;
    private String email;
    private String Number;

    public userinfo() {

    }

    public userinfo(String name, String email, String Number) {
        this.name = name;
        this.email = email;
        this.Number = Number;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return Number;
    }

    public String getEmail() {
        return email;
    }
}
