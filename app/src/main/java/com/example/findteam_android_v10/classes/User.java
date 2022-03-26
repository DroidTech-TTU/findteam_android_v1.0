package com.example.findteam_android_v10.classes;

import java.util.List;

public class User {
    private int uid;
    private String first_name;
    private String middle_name;
    private String last_name;
    private String email;
    private String passwords;
    private List<Project> projects;

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public User(int uid, String first_name, String middle_name, String last_name, String email, String passwords, List<Project> projects, String picture) {
        this.uid = uid;
        this.first_name = first_name;
        this.middle_name = middle_name;
        this.last_name = last_name;
        this.email = email;
        this.passwords = passwords;
        this.projects = projects;
        this.picture = picture;
    }

    public User() {
    }

    private String picture;

    public void setUid(int uid) {
        this.uid = uid;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setMiddle_name(String middle_name) {
        this.middle_name = middle_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswords(String passwords) {
        this.passwords = passwords;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getUid() {
        return uid;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getMiddle_name() {
        return middle_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswords() {
        return passwords;
    }

    public String getPicture() {
        return picture;
    }
}
