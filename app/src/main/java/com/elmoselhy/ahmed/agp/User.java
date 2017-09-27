package com.elmoselhy.ahmed.agp;

/**
 * Created by ahmedelmoselhy on 9/14/2017.
 */

public class User {
    String name;
    String email;
    String type;
    String image;
    String subject;
    String day;
    String hour;


    public User() {
    }

    public User(String name, String email, String type, String subject, String image) {
        this.name = name;
        this.email = email;
        this.type = type;
        this.image = image;
        this.subject = subject;

    }

    public User(String name, String email, String type, String subject, String department, String image) {
        this.name = name;
        this.email = email;
        this.type = type;
        this.image = image;
        this.subject = subject;
        this.department = department;

    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getType() {
        return type;
    }

    public String getImage() {
        return image;
    }

    public String getSubject() {
        return subject;
    }

    public String getDepartment() {
        return department;
    }

    public String getDay() {
        return day;
    }

    public String getHour() {
        return hour;
    }

    public String getMinute() {
        return minute;
    }

    String minute;
    String department ;

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }


}
