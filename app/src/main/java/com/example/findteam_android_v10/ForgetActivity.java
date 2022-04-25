package com.example.findteam_android_v10;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

public class ForgetActivity extends AppCompatActivity {

    //used for internal testing
    public static final String TAG = "ForgetActivity";

    //Uri used to retrieve information when user clicked forgot password in email
    public static Uri data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_main);

        // Grabs the url when the user redirects to the app in forgot password process
        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();

        //get uri information
        if (savedInstanceState == null && data != null) {

            Bundle bundle = new Bundle();
            try {

                //pass the access token to the activity to change the password
                bundle.putString("access_token", URLDecoder.decode(data.getEncodedQuery().replace("access_token=", ""), StandardCharsets.UTF_8.toString()));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            //navigate to the new password activity
            Navigation.findNavController(this, R.id.activity_forget_host_fragment).navigate(R.id.meNewPass, bundle);
        }
    }

}