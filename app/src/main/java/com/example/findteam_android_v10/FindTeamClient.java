package com.example.findteam_android_v10;


import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.HttpEntity;

public class FindTeamClient {

    private static final String BASE_URL = "https://findteam.2labz.com/";
    private static final AsyncHttpClient client = new AsyncHttpClient();
    public static String TAG = "FindTeamClient";

    public static void get(Context context, String url, HttpEntity entity, AsyncHttpResponseHandler responseHandler) {
        client.get(context, getAbsoluteUrl(url), entity, "application/json", responseHandler);
    }

    public static RequestHandle getObject(String url, AsyncHttpResponseHandler responseHandler) {
        return client.get(getAbsoluteUrl(url), responseHandler);
    }

    public static void get(String url, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), responseHandler);
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        Log.d(TAG, getAbsoluteUrl(url));
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(Context context, String url, HttpEntity entity, AsyncHttpResponseHandler responseHandler) {
        client.post(context, getAbsoluteUrl(url), entity, "application/json", responseHandler);
    }


    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        Log.d(TAG, getAbsoluteUrl(url));
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void delete(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        Log.d(TAG, getAbsoluteUrl(url));
        client.delete(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, AsyncHttpResponseHandler responseHandler) {
        Log.d(TAG, getAbsoluteUrl(url));
        client.post(getAbsoluteUrl(url), responseHandler);
    }

    public static void setAuth(String accessToken) {
        client.setBearerAuth(accessToken);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

}
