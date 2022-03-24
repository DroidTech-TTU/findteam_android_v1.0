package com.example.findteam_android_v10;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.util.EntityUtils;

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = "RegisterActivity";
    TextInputLayout etFirstName, etMiddleName, etLastName, etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Button registerBtn;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerBtn = findViewById(R.id.registerAct_btn);
        etFirstName = ((TextInputLayout) findViewById(R.id.etFirstName));
        etMiddleName = ((TextInputLayout) findViewById(R.id.etMiddleName));
        etLastName = ((TextInputLayout) findViewById(R.id.etLastName));
        etEmail = ((TextInputLayout) findViewById(R.id.etEmailAddress));
        etPassword = ((TextInputLayout) findViewById(R.id.etPassword));

        //does the process after the user register
        registerBtn.setOnClickListener(view -> {

            try {

                if(etEmail.getEditText().getText().toString().isEmpty()) etEmail.setError("Invalid email. Please enter email address");
                if(etPassword.getEditText().getText().toString().isEmpty()) etPassword.setError("Invalid password. Please enter passsword");
                createNewUser();
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }

            //Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            //startActivity(intent);
        });

    }

    void createNewUser() throws JSONException, IOException {

        RequestParams params = new RequestParams();
        JSONObject user = new JSONObject();
        user.put("first_name", etFirstName.getEditText().getText().toString());
        user.put("middle_name", etMiddleName.getEditText().getText().toString());
        user.put("last_name", etLastName.getEditText().getText().toString());
        user.put("email", etEmail.getEditText().getText().toString());
        user.put("password", etPassword.getEditText().getText().toString());

        StringEntity entity = new StringEntity(user.toString());

        Log.i(TAG,EntityUtils.toString(entity));
        FindTeamClient.post(this,"register", entity, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i(TAG, "the status code for this request is: " + statusCode);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.i(TAG, "It failed!" + statusCode);
            }
        });
    }


}