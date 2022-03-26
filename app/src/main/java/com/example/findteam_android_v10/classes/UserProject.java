package com.example.findteam_android_v10.classes;

public class UserProject {
    String role;
    String status;
    Project project;
    User user;

    static String TAG = "UserProject Class: ";

    public Project getProject() {
        return project;
    }

    public UserProject(String role, String status, Project project, User user) {
        this.role = role;
        this.status = status;
        this.project = project;
        this.user = user;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static String getTAG() {
        return TAG;
    }

    public static void setTAG(String TAG) {
        UserProject.TAG = TAG;
    }

    public UserProject() {

    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public String getStatus() {
        return status;
    }

}
