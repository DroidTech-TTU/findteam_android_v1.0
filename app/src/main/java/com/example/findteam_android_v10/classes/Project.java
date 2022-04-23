package com.example.findteam_android_v10.classes;

import android.util.Log;

import com.example.findteam_android_v10.FindTeamClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Project extends JSONObject {
    public static final String GET_PROJECT_API_URL = "project?pid=";

    public static final int STATUS_IN_UNKNOWN_INT = -1;
    public static final String STATUS_IN_UNKNOWN_STRING = "UNKNOWN";

    public static final int STATUS_IN_PROGRESS_INT = 1;
    public static final String STATUS_IN_PROGRESS_STRING = "In Progress";
    public static final String STATUS_IN_PROGRESS_ICON = "ic_project_status_in_progress_green";

    public static final int STATUS_IN_AWAITING_INT = 0;
    public static final String STATUS_IN_AWAITING_STRING = "Awaiting";
    public static final String STATUS_PENDING_ICON = "ic_project_status_in_pending_green";

    public static final int STATUS_IN_FINISHED_INT = 2;
    public static final String STATUS_FINISHED_STRING = "Completed";
    public static final String STATUS_FINISHED_ICON = "ic_project_status_in_finished_green";


    public static final int MEMBER_SHIP__TYPE_PENDING = 0;
    public static final int MEMBER_SHIP__TYPE_MEMBER = 1;
    public static final int MEMBER_SHIP__TYPE_OWNER = 3;
    public static final int MEMBER_SHIP__TYPE_GUEST = -1;
    public static final int MEMBER_SHIP__TYPE_ADMIN = 2;

    public static final String TAG = "ProjectClass";

    public static String getURLDeleteProject(int pid) {
        Log.d(TAG, "DeleteProjectURL=" + "project?pid=" + pid);
        return "project?pid=" + pid;
    }

    public static String getURLGetProject(int pid) {
        return "project?pid=" + pid;
    }

    public static String getURLJoinProject(int pid) {
        return "project/join?pid=" + pid;
    }

    public static String getURLDeletePicture(int pid, String imageName) {
        return "project/picture?pid=" + pid + "&picture_file=" + imageName;
    }

    public static String getURLUpdateProject(int pid) {
        return "project?pid=" + pid;
    }

    public static String getStringStatus(int i) {
        String statusString = STATUS_IN_UNKNOWN_STRING;
        switch (i){
            case STATUS_IN_AWAITING_INT:{
                statusString = STATUS_IN_AWAITING_STRING;
                break;
            }
            case STATUS_IN_PROGRESS_INT:{
                statusString = STATUS_IN_PROGRESS_STRING;
                break;
            }
            case STATUS_IN_FINISHED_INT:{
                statusString = STATUS_FINISHED_STRING;
                break;
            }
            default:{
                statusString = STATUS_IN_UNKNOWN_STRING;
                break;
            }
        }
        return statusString;
    }

    public static List<String> getTagsList(JSONObject project) throws JSONException {
        List<String> tags = new ArrayList<>();
        JSONArray pTags = project.optJSONArray("tags");
        Log.d(TAG, pTags.toString());
        for (int i = 0; i < pTags.length(); i++) {
            JSONObject tag = pTags.getJSONObject(i);
            Log.d(TAG, tag.toString());
            tags.add(tag.getString("text"));
        }
        Log.d(TAG, "tags = " + tags.toString());
        return tags;
    }

    public static JSONArray search(JSONArray jsonArrayProjects, String searchKey) throws JSONException {
        String[] keys = searchKey.split(" ");
        JSONArray results = new JSONArray();

        for (int i = 0; i < jsonArrayProjects.length(); i++) {
            JSONObject project = jsonArrayProjects.getJSONObject(i);

            if (isContainKeys(project.getString("title"), keys)) {
                results.put(project);
                continue;
            }

            JSONArray tags = project.getJSONArray("tags");
            for (int j = 0; j < tags.length(); j++) {
                if (isContainKeys(tags.getJSONObject(j).getString("text"), keys)) {
                    results.put(project);
                    break;
                }
            }

        }
        return results;
    }
    public static JSONArray searchMultiConditions(JSONArray jsonArrayProjects, String searchKey) throws JSONException {
        String[] keys = searchKey.split(" ");
        JSONArray results = new JSONArray();
        for (int i = 0; i < jsonArrayProjects.length(); i++) {
            JSONObject project = jsonArrayProjects.getJSONObject(i);
            int validCount = 0;
            for(int j=0; j<keys.length; j++){
                String key = keys[j].trim().toLowerCase();
                if(project.getString("title").trim().toLowerCase().contains(key)){
                    validCount++;
                    continue;
                }

                JSONArray tags = project.getJSONArray("tags");
                for (int k = 0; k < tags.length(); k++) {
                    if(tags.getJSONObject(k).getString("text").trim().toLowerCase().contains(key)){
                        validCount++;
                        break;
                    }
                }
            }
            if(validCount == keys.length){
                results.put(project);
            }
        }
        return results;
    }

    public static List<String> getPictures(JSONObject project) throws JSONException {
        List<String> pictureURLs = new ArrayList<>();
        JSONArray jsonArray = project.getJSONArray("pictures");
        for (int i = 0; i < jsonArray.length(); i++) {
            pictureURLs.add(Picture.GET_PICTURE_URL + jsonArray.get(i).toString());
        }
        return pictureURLs;
    }

    public static String getMemTypeString(int memTypeInt) {
        Log.d(TAG, "Member Type = " + memTypeInt);
        switch (memTypeInt) {
            case MEMBER_SHIP__TYPE_OWNER: {
                return "Owner";
            }
            case MEMBER_SHIP__TYPE_PENDING: {
                return "Pending";
            }
            case MEMBER_SHIP__TYPE_MEMBER: {
                return "Member";
            }
            case MEMBER_SHIP__TYPE_ADMIN: {
                return "Admin";
            }
            default: {
                return "Guest";
            }

        }
    }

    public static int getUserMembershipType(int uid, JSONObject project) throws JSONException {
        JSONArray mems = project.getJSONArray("members");
        if(uid == project.getInt("owner_uid")) return MEMBER_SHIP__TYPE_OWNER;
        for (int i = 0; i < mems.length(); i++) {
            if (uid == mems.getJSONObject(i).getInt("uid")) {
                return mems.getJSONObject(i).getInt("membership_type");
            }
        }
        return MEMBER_SHIP__TYPE_GUEST;
    }

    public static void getMyProjects(int uid, AsyncHttpResponseHandler asyncHttpResponseHandler) {

        FindTeamClient.get("project/search?uid=" + uid, asyncHttpResponseHandler);

    }

    public static void printProjects(String callTag, JSONArray projects) throws JSONException {
        Log.d(TAG, callTag + "-----------------------------------");
        for (int i = 0; i < projects.length(); i++) {
            Log.d(TAG, callTag + "Project: " + projects.getJSONObject(i));
        }
        Log.d(TAG, callTag + "====================================");
    }

    private static boolean isContainKeys(String s, String[] keys) {
        for (String key : keys) {

            if (s.trim().toLowerCase().contains(key.trim().toLowerCase())) return true;
        }
        return false;
    }

}
