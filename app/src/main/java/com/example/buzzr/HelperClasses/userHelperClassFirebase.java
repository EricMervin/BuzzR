package com.example.buzzr.HelperClasses;

public class userHelperClassFirebase {
    String name, username, phoneNo, password, counter;

    public userHelperClassFirebase(String userName, String userUsername, String userPhoneNumber, String userPassword, String userCounter) {
        this.name = userName;
        this.username = userUsername;
        this.phoneNo = userPhoneNumber;
        this.password = userPassword;
        this.counter = userCounter;
    }

    public userHelperClassFirebase() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCounter() {
        return counter;
    }

    public void setCounter(String counter) {
        this.counter = counter;
    }
}
