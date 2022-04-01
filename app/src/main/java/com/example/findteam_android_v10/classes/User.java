package com.example.findteam_android_v10.classes;

import android.util.Log;

import com.example.findteam_android_v10.FindTeamClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.List;

public class User {
    public static final String GET_USER_URL = "user?uid=";
    public static final int MEMBER_SHIP__TYPE_OWNER = 0;
    public static final int MEMBER_SHIP__TYPE_PENDING = 1;
    public static final int MEMBER_SHIP__TYPE_MEMBER = 2;
    public static String TAG = "UserClass";

}
