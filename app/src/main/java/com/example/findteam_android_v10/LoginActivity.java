package com.example.findteam_android_v10;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.example.findteam_android_v10.classes.User;
import com.google.android.material.textfield.TextInputLayout;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;

import cz.msebera.android.httpclient.Header;


public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    public static JSONObject currentUser;
    private SharedPreferences mPrefs;

    private String masterKeyAlias;
    public static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Elements present in the app
        Button loginInLoginBtn;
        TextView forgotPass, signUpText;
        TextInputLayout username, password;

        setContentView(R.layout.activity_login);

        try {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            sharedPreferences = EncryptedSharedPreferences.create(
                    "FindTeam",
                    masterKeyAlias,
                    this,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

        } catch (GeneralSecurityException |  IOException e) {
            e.printStackTrace();
        }

        //Grabbing the elements on the UI
        loginInLoginBtn = findViewById(R.id.login_btn);
        forgotPass = findViewById(R.id.forgot_pass);
        signUpText = findViewById(R.id.signup_text);
        username = findViewById(R.id.email);
        password = findViewById(R.id.password);

        //checks to see if the user is persisted throughout the app, go login directly
        //sharedPreferences = getSharedPreferences("FindTeam", MODE_PRIVATE);
        if(!sharedPreferences.getString("access_token", "").equals("")) {

            ProgressDialog progressDialog = ProgressDialog.show(this, "Loading", "Logging in", true);
            FindTeamClient.setAuth(sharedPreferences.getString("access_token", ""));

            User.getCurrentUser(new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    LoginActivity.currentUser = response;

                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                    progressDialog.dismiss();

                    finish();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    throwable.printStackTrace();
                    sharedPreferences.edit().putString("access_token","").apply();
                    Toast.makeText(LoginActivity.this, "Connection Failed. Please try again!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }

            });
        }

        //signup account
        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        //logging in for the first time
        loginInLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //grab the user and pass inputted text
                String user = username.getEditText().getText().toString(),
                        pass = password.getEditText().getText().toString();

                User.loginUser(LoginActivity.this, user, pass);
            }
        });

        //forgot password
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgetActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });



    }
}