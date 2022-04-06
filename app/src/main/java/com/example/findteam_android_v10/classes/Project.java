package com.example.findteam_android_v10.classes;

import android.util.Log;
import android.util.Pair;

import com.example.findteam_android_v10.FindTeamClient;
import com.example.findteam_android_v10.LoginActivity;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class Project extends JSONObject {
    public static final String GET_PROJECT_API_URL = "project?pid=";
    public static final int STATUS_IN_PROGRESS_INT = 0;
    public static final String STATUS_IN_PROGRESS_STRING = "In Progress";
    public static final String STATUS_IN_PROGRESS_ICON = "ic_project_status_in_progress_green";

    public static final int STATUS_IN_PENDING_INT = 1;
    public static final String STATUS_PENDING_STRING = "Pending";
    public static final String STATUS_PENDING_ICON = "ic_project_status_in_pending_green";

    public static final int STATUS_IN_FINISHED_INT = 2;
    public static final String STATUS_FINISHED_STRING = "Finished";
    public static final String STATUS_FINISHED_ICON = "ic_project_status_in_finished_green";

    public static final int MEMBER_SHIP__TYPE_OWNER = 2;
    public static final int MEMBER_SHIP__TYPE_PENDING = 1;
    public static final int MEMBER_SHIP__TYPE_MEMBER = 0;
    public static final int MEMBER_SHIP__TYPE_GUEST= 3;

    public static final String TAG = "ProjectClass";
    public static final String getURLDeleteProject(int pid){
        Log.d(TAG, "DeleteProjectURL=" + "project?pid=" + pid);
        return "project?pid=" + pid;
    }
    public static final String getURLGetProject(int pid){return "project?pid=" + pid;}
    public static final String getURLJoinProject(int pid){return "project/join?pid=" + pid;}
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
        JSONArray pTags = project.optJSONArray("tags");
        Log.d(TAG, pTags.toString());
        for(int i =0; i< pTags.length(); i++){
            JSONObject tag = pTags.getJSONObject(i);
            Log.d(TAG, tag.toString());
            tags.add(tag.getString("text"));
        }
        Log.d(TAG, "tags = " + tags.toString());
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
    public static List<String> getPictures(JSONObject project) throws JSONException {
        List<String> pictureURLs = new ArrayList<>();
        JSONArray jsonArray = project.getJSONArray("pictures");
        for(int i = 0; i< jsonArray.length(); i++){
            pictureURLs.add(Picture.GET_PICTURE_URL +jsonArray.get(i).toString());
        }
        return pictureURLs;
    }

    public static String getMemTypeString(int memTypeInt){
        Log.d(TAG, "Member Type = " + memTypeInt);
        switch (memTypeInt){
            case MEMBER_SHIP__TYPE_OWNER:{
                return "Owner";
            }
            case MEMBER_SHIP__TYPE_PENDING:{
                return "Pending";
            }
            case MEMBER_SHIP__TYPE_MEMBER:{
                return "Member";
            }
            default:{
                return "Guest";
            }

        }
    }
    public static HashMap<Integer, List<String>> getStatus(int i){

        switch (i){
            case 0:{
                List<String> status = new ArrayList<>();
                status.add(STATUS_IN_PROGRESS_STRING);
                status.add(STATUS_IN_PROGRESS_ICON);
                HashMap<Integer, List<String>> statusMap = new HashMap<>();
                statusMap.put(i, status);
                return statusMap;
            }
            case 1:{
                List<String> status = new ArrayList<>();
                status.add(STATUS_PENDING_STRING);
                status.add(STATUS_PENDING_ICON);
                HashMap<Integer, List<String>> statusMap = new HashMap<>();
                statusMap.put(i, status);
                return statusMap;
            }
            default:{
                List<String> status = new ArrayList<>();
                status.add(STATUS_FINISHED_STRING);
                status.add(STATUS_FINISHED_ICON);
                HashMap<Integer, List<String>> statusMap = new HashMap<>();
                statusMap.put(i, status);
                return statusMap;
            }
        }
    }

    public static int getUserMembershipType(int uid, JSONObject project) throws JSONException {
        JSONArray mems = project.getJSONArray("members");
        for(int i=0; i<mems.length(); i++){
            if(uid == mems.getJSONObject(i).getInt("uid")){
                return mems.getJSONObject(i).getInt("membership_type");
            }
        }
        return MEMBER_SHIP__TYPE_GUEST;
    }

    public static void getAllProjects(AsyncHttpResponseHandler asyncHttpResponseHandler){


    };

    public static void getMyProjects(AsyncHttpResponseHandler asyncHttpResponseHandler){

        try {
            FindTeamClient.get("search/uid=" + LoginActivity.currentUser.getString("uid"), asyncHttpResponseHandler);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

}
