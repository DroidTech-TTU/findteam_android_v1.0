package com.example.findteam_android_v10.classes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.findteam_android_v10.FindTeamClient;
import com.example.findteam_android_v10.LoginActivity;
import com.example.findteam_android_v10.MainActivity;
import com.example.findteam_android_v10.R;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import androidx.navigation.Navigation;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class User {

    private static final String KEY_USER = "user";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_LOGIN = "login";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PICTURE = "picture";
    public static final String KEY_USER_PICTURE = "user/picture";
    public static final String KEY_FORGOT_PASSWORD = "user/reset?email=";
    public static final String GET_USER_URL = "user?uid=";
    public static final String KEY_SEARCH = "user/search";
    public static String TAG = "UserClass";


    //update the Login user for new information
    public static void getCurrentUser() {

        FindTeamClient.get(KEY_USER, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                LoginActivity.currentUser = response;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                throwable.printStackTrace();
            }
        });

    }

    //update the LoginUser and go to main
    public static void getCurrentUser(AsyncHttpResponseHandler asyncHttpResponseHandler) {

        FindTeamClient.get(KEY_USER, asyncHttpResponseHandler);
    }

    public static void getAllUser(AsyncHttpResponseHandler asyncHttpResponseHandler) {

        FindTeamClient.get(KEY_SEARCH, asyncHttpResponseHandler);

    }

    public static void getUserByUid(RequestParams params, AsyncHttpResponseHandler asyncHttpResponseHandler) {

        FindTeamClient.get(KEY_USER, params, asyncHttpResponseHandler);

    }

    public static void changeProfilePic(Context context, Uri profPic, AsyncHttpResponseHandler asyncHttpResponseHandler) {

        try {

            //update the profile picture
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            Bitmap map = null;

            map = MediaStore.Images.Media.getBitmap(context.getContentResolver(), profPic);
            File f = new File(context.getCacheDir(), map.toString() + ".jpeg");
            f.createNewFile();
            map.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] image = stream.toByteArray();

            //write binary to jpeg file
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(image);
            fos.flush();
            fos.close();

            RequestParams params = new RequestParams();

            params.put(KEY_PICTURE, f, "image/jpeg");

            FindTeamClient.post(KEY_USER_PICTURE, params, asyncHttpResponseHandler);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateUser(Context context, JSONObject user, AsyncHttpResponseHandler asyncHttpResponseHandler) throws UnsupportedEncodingException {
        StringEntity entity = new StringEntity(user.toString());

        FindTeamClient.post(context, "user", entity, asyncHttpResponseHandler);

    }

    public static void loginUser(Context context, String email, String password) {

        //create a parameter to pass to the client
        RequestParams params = new RequestParams();
        params.put("grant_type", KEY_PASSWORD);
        params.put(KEY_USERNAME, email);
        params.put(KEY_PASSWORD, password);

        //POST LOGIN
        FindTeamClient.post(KEY_LOGIN, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {

                    //login success

                    //temporarily store login access_token
                    LoginActivity.sharedPreferences.edit().putString("access_token", response.getString("access_token")).apply();

                    //set the auth for logging in
                    FindTeamClient.setAuth(response.getString("access_token"));

                    //retrieve the user from the API
                    User.getCurrentUser();

                    Intent i = new Intent(context, MainActivity.class);
                    context.startActivity(i);

                    ((Activity) context).finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                //Invalid credentials
                Toast.makeText(context, "Invalid Credential. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void sendResetEmail(String email) {

        FindTeamClient.post(KEY_FORGOT_PASSWORD + email, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                //success sent
            }

        });
    }

    public static void resetPass(Context context, View view, String newPass) {

        FindTeamClient.get(KEY_USER, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {

                    //add the new password to update the user
                    response.put(KEY_PASSWORD, newPass);
                    StringEntity entity = new StringEntity(response.toString());

                    FindTeamClient.post(context, KEY_USER, entity, new TextHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {
                            Navigation.findNavController(view).navigate(R.id.action_meNewPass_to_meResetSuccess);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    });

                } catch (JSONException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    public static JSONArray searchMultiConditions(JSONArray users, String searchKey) throws JSONException {
        String[] keys = searchKey.split(" ");
        JSONArray results = new JSONArray();
        for (int i = 0; i < users.length(); i++) {
            JSONObject project = users.getJSONObject(i);
            int validCount = 0;
            for(int j=0; j<keys.length; j++){
                String key = keys[j].trim().toLowerCase();
                if(project.getString("first_name").trim().toLowerCase().contains(key)
                    || (project.getString("last_name").trim().toLowerCase().contains(key))){
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

    public static JSONArray searchUsers(JSONArray users, String searchKey) throws JSONException {
        String[] keys = searchKey.split(" ");
        for (String key : keys) {
            Log.d(TAG, key);
        }

        JSONArray results = new JSONArray();
        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.getJSONObject(i);
            if (isContainKeys(user.getString("first_name"), keys)
                    || isContainKeys(user.getString("last_name"), keys)
                    || isContainKeys(user.getString("last_name"), keys)) {
                results.put(user);
//                continue;
            } else {
                JSONArray tags = user.getJSONArray("tags");
                for (int j = 0; j < tags.length(); j++) {
                    if (isContainKeys(tags.getJSONObject(j).getString("text"), keys)) {
                        results.put(user);
                        break;
                    }
                }
            }
        }
        Log.d(TAG, results.toString());
        return results;
    }

    private static boolean isContainKeys(String s, String[] keys) {
        for (String key : keys) {

            if (s.trim().toLowerCase().contains(key.trim().toLowerCase())) return true;
        }
        return false;
    }

    public User() {
    }
}
