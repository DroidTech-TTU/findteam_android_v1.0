package com.example.findteam_android_v10;

import static androidx.navigation.Navigation.findNavController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.findteam_android_v10.fragments.FragForgotNewPassDirections;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class ForgetActivity extends AppCompatActivity {

    public static Uri data;
    public static final String TAG = "ForgetActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_main);

        // Get the url the user clicked to launch the app
        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();

        //get uri information
        if(savedInstanceState == null && data != null) {

            Bundle bundle = new Bundle();
            try {
                bundle.putString("access_token", URLDecoder.decode(data.getEncodedQuery().replace("access_token=", ""), StandardCharsets.UTF_8.toString()));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Navigation.findNavController(this, R.id.activity_forget_host_fragment).navigate(R.id.meNewPass, bundle);
        }
    }

}