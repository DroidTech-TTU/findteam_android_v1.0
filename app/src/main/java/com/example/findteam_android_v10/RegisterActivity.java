package com.example.findteam_android_v10;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import java.util.HashSet;
import java.util.Set;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.util.EntityUtils;

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = "RegisterActivity";
    TextInputLayout etFirstName, etMiddleName, etLastName, etEmail, etPassword;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Button registerBtn;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        context = this;

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

                if(!etEmail.getEditText().getText().toString().isEmpty() && !etPassword.getEditText().getText().toString().isEmpty()) {
                    createNewUser();

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    void createNewUser() throws JSONException, IOException, Exception {

        RequestParams params = new RequestParams();
        JSONObject user = new JSONObject();
        user.put("first_name", etFirstName.getEditText().getText().toString());
        user.put("middle_name", etMiddleName.getEditText().getText().toString());
        user.put("last_name", etLastName.getEditText().getText().toString());
        user.put("email", etEmail.getEditText().getText().toString());
        user.put("password", etPassword.getEditText().getText().toString());

        //Add to SharedPreference the email and access token and persist == 0 (default)

        StringEntity entity = new StringEntity(user.toString());
        FindTeamClient.post(this,"register", entity, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i(TAG, "the status code for this request is: " + statusCode);
                Toast.makeText(context, "Successfully Created Account", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, "the status code for this request is" + statusCode);
                Toast.makeText(context, "Error creating Account. Email already been used. Please enter a new one.", Toast.LENGTH_LONG).show();
            }
        });
    }


}