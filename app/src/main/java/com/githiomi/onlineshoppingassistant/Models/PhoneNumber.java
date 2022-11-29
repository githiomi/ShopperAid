package com.githiomi.onlineshoppingassistant.Models;

public class PhoneNumber {

    String username;
    String phoneNumber;

    public PhoneNumber() {
    }

    public PhoneNumber(String username, String phoneNumber) {
        this.username = username;
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
