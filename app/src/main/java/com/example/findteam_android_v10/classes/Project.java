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
    public static final int MEMBER_SHIP__TYPE_OWNER = 2;
    public static final int MEMBER_SHIP__TYPE_GUEST = 3;
    public static final int MEMBER_SHIP__TYPE_REJECT = 4;

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
        return 0 == i ? "In Progress" : "Done";
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
            case MEMBER_SHIP__TYPE_REJECT: {
                return "Reject";
            }
            default: {
                return "Guest";
            }

        }
    }

    public static HashMap<Integer, List<String>> getStatus(int i) {

        switch (i) {
            case 0: {
                List<String> status = new ArrayList<>();
                status.add(STATUS_IN_PROGRESS_STRING);
                status.add(STATUS_IN_PROGRESS_ICON);
                HashMap<Integer, List<String>> statusMap = new HashMap<>();
                statusMap.put(i, status);
                return statusMap;
            }
            case 1: {
                List<String> status = new ArrayList<>();
                status.add(STATUS_IN_AWAITING_STRING);
                status.add(STATUS_PENDING_ICON);
                HashMap<Integer, List<String>> statusMap = new HashMap<>();
                statusMap.put(i, status);
                return statusMap;
            }
            default: {
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
