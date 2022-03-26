package com.example.findteam_android_v10;

import static androidx.navigation.Navigation.findNavController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

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
            //Log.i(TAG, data.getEncodedQuery());
            Navigation.findNavController(this, R.id.activity_forget_host_fragment).navigate(R.id.meNewPass);
        }
    }

}