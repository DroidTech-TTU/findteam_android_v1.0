package com.example.findteam_android_v10;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.ui.NavigationUI;

import static androidx.navigation.Navigation.findNavController;

public class MainActivity extends AppCompatActivity {

    //TAG for internal testing
    private static final String TAG = "MainActivity";

    //request to go to chat history
    public static final int REQUEST_CHAT_HISTORY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //bind the navigation controller and bottom navigationview
        NavController navController = findNavController(this, R.id.activity_main_nav_host_fragment);
        BottomNavigationView bottomNavigationView = findViewById(R.id.activity_main_bottom_navigation_view);

        Intent i = getIntent();

        //checks to see if the activity is requesting to go the chat history
        if (i.getIntExtra("request", -1) == REQUEST_CHAT_HISTORY) {

            int puid = i.getIntExtra("puid", -1);
            boolean is_user = i.getBooleanExtra("is_user", false);
            String title = i.getStringExtra("title");
            int request = i.getIntExtra("request", -1);

            Bundle bun = new Bundle();
            bun.putInt("puid", puid);
            bun.putBoolean("is_user", is_user);
            bun.putString("title", title);
            bun.putInt("request", request);


            navController.navigate(R.id.item_chat_history, bun);
        }
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

}