package com.example.findteam_android_v10;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.ui.NavigationUI;

import static androidx.navigation.Navigation.findNavController;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final int REQUEST_CHAT_HISTORY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavController navController = findNavController(this, R.id.activity_main_nav_host_fragment);
        BottomNavigationView bottomNavigationView = findViewById(R.id.activity_main_bottom_navigation_view);

        Intent i = getIntent();
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

//            FragChatListDirections.ActionItemChatListToItemChatHistory action =
//                    FragChatListDirections.actionItemChatListToItemChatHistory(is_user, puid, fullName, request);
//            navController.navigate(action);
        }
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

}