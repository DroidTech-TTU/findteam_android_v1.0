package com.example.findteam_android_v10;

import static androidx.navigation.Navigation.findNavController;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.findteam_android_v10.fragments.FragChatListDirections;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
        if(i.getIntExtra("request", -1) == REQUEST_CHAT_HISTORY){

            int puid = i.getIntExtra("puid", -1);
            Boolean is_user = i.getBooleanExtra("is_user", false);
            String fullName = i.getStringExtra("fullName");
            int request = i.getIntExtra("request", -1);
            Log.d(TAG, "puid = " + puid);
            Log.d(TAG, "is_user = " + is_user);
            Log.d(TAG, "title = " + fullName);
            Log.d(TAG, "request = " + request);

            Bundle bun = new Bundle();
            bun.putInt("puid", puid);
            bun.putBoolean("is_user", is_user);
            bun.putString("title",fullName);
            bun.putInt("request", request);

            navController.navigate(R.id.item_chat_history, bun);

//            FragChatListDirections.ActionItemChatListToItemChatHistory action =
//                    FragChatListDirections.actionItemChatListToItemChatHistory(is_user, puid, fullName, request);
//            navController.navigate(action);
        }
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}