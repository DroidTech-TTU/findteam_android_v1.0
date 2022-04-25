package com.example.findteam_android_v10;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.findteam_android_v10.classes.User;
import com.google.android.material.textfield.TextInputLayout;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;
import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {

    //TAG for internal testing
    public static final String TAG = "LoginActivity";

    //persisted User after a user logged in
    public static JSONObject currentUser;

    //sharedPreference to retrieve the access token
    public static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Elements present in the app
        Button loginInLoginBtn;
        TextView forgotPass, signUpText;
        TextInputLayout username, password;

        setContentView(R.layout.activity_login);

        //setup to encrypt the access token in the android device using AES256 authentication
        try {
            sharedPreferences = EncryptedSharedPreferences.create(
                    "FindTeam",
                    MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                    this,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        //Bind the variables to the elements
        loginInLoginBtn = findViewById(R.id.login_btn);
        forgotPass = findViewById(R.id.forgot_pass);
        signUpText = findViewById(R.id.signup_text);
        username = findViewById(R.id.email);
        password = findViewById(R.id.password);

        //checks to see if the user is persisted throughout the app, go login directly
        if (!sharedPreferences.getString("access_token", "").equals("")) {

            //progress dialog ui
            ProgressDialog progressDialog = ProgressDialog.show(this, "Loading", "Logging in", true);

            //retrieve the access token
            FindTeamClient.setAuth(sharedPreferences.getString("access_token", ""));

            //call the api to get the updated user
            User.getCurrentUser(new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    LoginActivity.currentUser = response;

                    //go to the main activity
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                    progressDialog.dismiss();

                    finish();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                    //print the error for internal testing
                    throwable.printStackTrace();

                    //remove the access token and make user relogin again if failed
                    sharedPreferences.edit().putString("access_token", "").apply();
                    Toast.makeText(LoginActivity.this, "Connection Failed. Please try again!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }

            });
        }

        //signup account
        signUpText.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        });

        //logging in for the first time
        loginInLoginBtn.setOnClickListener(view -> {

            //extract the user and pass inputted text
            String user = username.getEditText().getText().toString(),
                    pass = password.getEditText().getText().toString();

            //login user
            User.loginUser(LoginActivity.this, user, pass);
        });

        //forgot password
        forgotPass.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, ForgetActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        });

    }
}