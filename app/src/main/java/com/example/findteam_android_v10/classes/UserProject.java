package com.example.findteam_android_v10.classes;

import android.util.Log;

import java.util.ArrayList;

public class UserProject {
    String userName;
    int projectId;
    String role;
    String status;
    static String TAG = "UserProject Class: ";
    public UserProject(int projectId, String userName, String role, String status) {
        this.userName = userName;
        this.projectId = projectId;
        this.role = role;
        this.status = status;
    }
    public UserProject() {

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getProjectId() {
        return projectId;
    }

    public String getRole() {
        return role;
    }

    public String getStatus() {
        return status;
    }

    public static ArrayList<UserProject> creatUserProjectsList(int num) {
        ArrayList<UserProject> members = new ArrayList<UserProject>();

        for (int i = 1; i <= num; i++) {
            members.add(new UserProject(1, "Username " + i, "Member", "Approved" ));
            Log.d(TAG, String.valueOf(i));
        }

        return members;
    }
}
