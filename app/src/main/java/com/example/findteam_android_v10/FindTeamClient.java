package com.example.findteam_android_v10;


import android.content.Context;
import android.util.Log;

import com.loopj.android.http.*;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;

public class FindTeamClient {

    private static final String BASE_URL = "https://findteam.2labz.com/";
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(Context context, String url, HttpEntity entity, AsyncHttpResponseHandler responseHandler) {
        client.get(context, getAbsoluteUrl(url), entity, "application/json", responseHandler);
    }

    public static void post(Context context, String url, HttpEntity entity, AsyncHttpResponseHandler responseHandler) {
        client.post(context, getAbsoluteUrl(url), entity, "application/json", responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, AsyncHttpResponseHandler responseHandler){
        client.post(getAbsoluteUrl(url), responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

}
