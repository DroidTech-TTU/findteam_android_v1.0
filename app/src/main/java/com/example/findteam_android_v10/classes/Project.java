package com.example.findteam_android_v10.classes;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Project extends JSONObject {
    public static final String TAG = "ProjectClass";
    public static final String getURLDeleteProject(int pid){
        return "project?pid=" + pid;
    }
    public static final String getURLDeletePicture(int pid, String imageName){
        return "project/picture?pid=" + pid + "&picture_file="+ imageName;
    }
    public static final String getURLUpdateProject(int pid){
        return "project?pid=" + pid;
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

    public static JSONArray search(JSONArray jsonArrayProjects, String searchKey) throws JSONException {
        searchKey = searchKey.trim().toLowerCase();
        JSONArray results = new JSONArray();
        for(int i=0; i<jsonArrayProjects.length(); i++){
            JSONObject project = jsonArrayProjects.getJSONObject(i);
            String title = project.getString("title").trim().toLowerCase();
            if(title.contains(searchKey) ) {
                results.put(project);
                continue;
            }

            JSONArray tags = project.getJSONArray("tags");
            for(int j = 0; j<tags.length(); j++){
                if(tags.getJSONObject(j).getString("text").trim().toLowerCase().contains(searchKey) ) {
                    results.put(project);
                    break;
                }
                if(tags.getJSONObject(j).getString("category").trim().toLowerCase().contains(searchKey) ) {
                    results.put(project);
                    break;
                }
            }

        }
        return results;
    }

    public static String getMemType(int memTypeInt){
        switch (memTypeInt){
            case 0:{
                return "Owner";
            }
            case 1:{
                return "Pending";
            }
            case 2:{
                return "Member";
            }
            default:{
                return "Unknown";
            }

        }
    }
}
