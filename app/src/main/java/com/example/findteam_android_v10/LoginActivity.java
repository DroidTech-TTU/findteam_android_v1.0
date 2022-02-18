package com.example.findteam_android_v10;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Button registerInLoginBtn, loginInLoginBtn;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        registerInLoginBtn = findViewById(R.id.register_btn);
        loginInLoginBtn = findViewById(R.id.login_btn);

        //register account
        registerInLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        //login account
        loginInLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
    public void loginSuccessful(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void registerAccount(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}