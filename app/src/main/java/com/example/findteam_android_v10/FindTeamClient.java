package com.example.findteam_android_v10;


import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.HttpEntity;

public class FindTeamClient {

    //base url for the FastAPI client
    private static final String BASE_URL = "https://findteam.2labz.com/";

    //create an AsyncHTTPClient
    private static final AsyncHttpClient client = new AsyncHttpClient();

    //TAG for internal testing
    public static String TAG = "FindTeamClient";

    // ===================================== GET REQUEST ==============================================================
    public static void get(Context context, String url, HttpEntity entity, AsyncHttpResponseHandler responseHandler) {
        client.get(context, getAbsoluteUrl(url), entity, "application/json", responseHandler);
    }

    public static void get(String url, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), responseHandler);
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }
    // ================================================================================================================

    // ===================================== POST REQUEST =============================================================
    public static void post(Context context, String url, HttpEntity entity, AsyncHttpResponseHandler responseHandler) {
        client.post(context, getAbsoluteUrl(url), entity, "application/json", responseHandler);
    }


    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), responseHandler);
    }

    // ================================================================================================================

    // ===================================== DELETE REQUEST ===========================================================
    public static void delete(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.delete(getAbsoluteUrl(url), params, responseHandler);
    }
    // ================================================================================================================


    //set bearer auth for all requests made
    public static void setAuth(String accessToken) {
        client.setBearerAuth(accessToken);
    }

    //combines the url with the base url
    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

}
