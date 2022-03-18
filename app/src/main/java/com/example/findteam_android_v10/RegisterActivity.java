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

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = "RegisterActivity222";
    EditText etFirstName, etMiddleName, etLastName, etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Button registerBtn;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerBtn = findViewById(R.id.registerAct_btn);
        etFirstName = ((TextInputLayout) findViewById(R.id.etFirstName)).getEditText();
        etMiddleName = ((TextInputLayout) findViewById(R.id.etMiddleName)).getEditText();
        etLastName = ((TextInputLayout) findViewById(R.id.etLastName)).getEditText();
        etEmail = ((TextInputLayout) findViewById(R.id.etEmailAddress)).getEditText();
        etPassword = ((TextInputLayout) findViewById(R.id.etPassword)).getEditText();

        //does the process after the user register
        registerBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                try {
                    createNewUser();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    void createNewUser() throws JSONException, UnsupportedEncodingException {

        RequestParams params = new RequestParams();

        JSONObject user = new JSONObject();
        user.put("first_name", etFirstName.getText().toString());
        user.put("middle_name", etMiddleName.getText().toString());
        user.put("last_name", etLastName.getText().toString());
        user.put("email", etEmail.getText().toString());
        user.put("password", etPassword.getText().toString());

        StringEntity entity = new StringEntity(user.toString());

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