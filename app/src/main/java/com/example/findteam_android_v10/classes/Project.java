package com.example.findteam_android_v10.classes;

import java.util.ArrayList;

public class Project {
    private String name;
    private String status;
    private int id;

    public Project(String name, String status, int id) {
        this.name = name;
        this.status = status;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }
    public int getId(){
        return id;
    }

    private static int lastProjectId = 0;
    public static ArrayList<Project> creatProjectsList(int numContacts) {
        ArrayList<Project> projects = new ArrayList<Project>();

        for (int i = 1; i <= numContacts; i++) {
            projects.add(new Project("Person " + ++lastProjectId, "Active", lastProjectId));
        }

        return projects;
    }
}
