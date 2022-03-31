package com.example.findteam_android_v10;

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

import com.example.findteam_android_v10.classes.User;
import com.google.android.material.textfield.TextInputLayout;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    public static JSONObject currentUser;
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Elements present in the app
        Button registerInLoginBtn, loginInLoginBtn;
        TextView forgotPass, signUpText;
        TextInputLayout username, password;
        SharedPreferences sharedPreferences;


        setContentView(R.layout.activity_login);

        //Grabbing the elements on the UI
        loginInLoginBtn = findViewById(R.id.login_btn);
        forgotPass = findViewById(R.id.forgot_pass);
        signUpText = findViewById(R.id.signup_text);
        username = findViewById(R.id.email);
        password = findViewById(R.id.password);

        //checks to see if the user is persisted throughout the app, go login directly
        sharedPreferences = getSharedPreferences("FindTeam", MODE_PRIVATE);
        if(!sharedPreferences.getString("access_token", "").equals("")){

            FindTeamClient.setAuth(sharedPreferences.getString("access_token", ""));

            //retrieve the user from the API
            FindTeamClient.get("user", new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    currentUser = response;
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e(TAG, statusCode + " " + responseString + " " + throwable);
                }
            });

            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
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


                //create a parameter to pass to the client
                RequestParams params = new RequestParams();
                params.put("grant_type", "password");
                params.put("username", user);
                params.put("password", pass);

                //POST LOGIN
                FindTeamClient.post("login", params, new JsonHttpResponseHandler(){

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                        Log.i(TAG, "the status code for this request is: " + statusCode);

                        try {

                            //login success

                            //temporarily store login access_token
                            sharedPreferences.edit().putString("access_token", response.getString("access_token")).apply();

                            //set the auth for logging in
                            FindTeamClient.setAuth(response.getString("access_token"));

                            //retrieve the user from the API
                            FindTeamClient.get("user", new JsonHttpResponseHandler(){
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    currentUser = response;
                                    Log.e(TAG, statusCode + " " + currentUser.toString());
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                    Log.e(TAG, statusCode + " " + responseString + " " + throwable);
                                }
                            });

                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            i.putExtra("access_token", sharedPreferences.getString("access_token", ""));
                            startActivity(i);

                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.e(TAG, "the status code for this request is: " + statusCode);

                        //Invalid credentials
                        Toast.makeText(LoginActivity.this, "Invalid Credential. Please try again.", Toast.LENGTH_SHORT).show();
                    }

                });
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