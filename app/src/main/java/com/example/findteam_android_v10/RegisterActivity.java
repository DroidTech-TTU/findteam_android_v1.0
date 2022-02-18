package com.example.findteam_android_v10;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RegisterActivity extends AppCompatActivity {

    ImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ImageView addPicBtn;
        Button registerBtn;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        profilePic = findViewById(R.id.profilePic);
        addPicBtn = findViewById(R.id.profPicBtn);
        registerBtn = findViewById(R.id.registerAct_btn);

        //get the profile image from the phone storage
        addPicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("image/*");
            }
        });

        //does the process after the user register
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    // Handle the returned Uri
                    profilePic.setImageURI(uri);
                }

            });


}