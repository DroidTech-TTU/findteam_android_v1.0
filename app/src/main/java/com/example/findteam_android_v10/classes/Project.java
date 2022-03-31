package com.example.findteam_android_v10.classes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Project extends JSONObject {
    private int id;
    private String title;
    private String status;
    private List<String> pictures;
    private int owner_uid;
    private List<String> tag;
    public List<User> getUsers() {
        return users;
    }

    public int getOwner_uid() {
        return owner_uid;
    }

    public void setOwner_uid(int owner_uid) {
        this.owner_uid = owner_uid;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }

    public static int getLastProjectId() {
        return lastProjectId;
    }

    public static void setLastProjectId(int lastProjectId) {
        Project.lastProjectId = lastProjectId;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public Project(String name, String status, int id, List<String> pictures, List<String> tags, List<User> users) {
        this.title = name;
        this.status = status;
        this.id = id;
        this.pictures = pictures;
        this.tags = tags;
        this.users = users;
    }

    private List<String> tags;
    private List<User> users;

    public void setName(String name) {
        this.title = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getPictures() {
        return pictures;
    }

    public void setPictures(List<String> pictures) {
        this.pictures = pictures;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    /**
     *
     * @param name
     * @param status
     * @param id
     */
    public Project(String name, String status, int id) {
        this.title = name;
        this.status = status;
        this.id = id;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return title;
    }

    public String getStatus() {
        return status;
    }
    public int getId(){
        return id;
    }


    //Fake data
    private static int lastProjectId = 0;
    public static ArrayList<Project> creatProjectsList(int numContacts) {
        ArrayList<Project> projects = new ArrayList<Project>();

        for (int i = 1; i <= numContacts; i++) {
            projects.add(new Project("Person " + ++lastProjectId, "Active", lastProjectId));
        }

        return projects;
    }

    public static String getStringStatus(int i){
        return 0==i?"In Progress":"Done";
    }
    public static List<String> getTagsList(JSONObject project) throws JSONException {
        List<String> tags = new ArrayList<>();

        if(project.getJSONArray("tags").length() >0){
            tags = Arrays.asList((project.getJSONArray("tags").getJSONObject(0).getString("text") + ","+project.getString("title")).split(","));
        }else{
            tags.add(project.getString("title"));
        }
       return tags;
    }
}
