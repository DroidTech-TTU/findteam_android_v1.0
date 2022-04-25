package com.example.findteam_android_v10;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.findteam_android_v10.classes.User;
import com.google.android.material.textfield.TextInputLayout;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class RegisterActivity extends AppCompatActivity {

    //TAG for internal testing
    public static final String TAG = "RegisterActivity";

    //elements needed to be retrieved
    private TextInputLayout etFirstName, etMiddleName, etLastName, etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Button registerBtn;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //bind the elements
        registerBtn = findViewById(R.id.registerAct_btn);
        etFirstName = findViewById(R.id.etFirstName);
        etMiddleName = findViewById(R.id.etMiddleName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmailAddress);
        etPassword = findViewById(R.id.etPassword);

        //does the process after the user register
        registerBtn.setOnClickListener(view -> {

            try {

                //if email is empty
                if (etEmail.getEditText().getText().toString().isEmpty())
                    etEmail.setError("Invalid email. Please enter email address");
                //if password is empty
                if (etPassword.getEditText().getText().toString().isEmpty())
                    etPassword.setError("Invalid password. Please enter passsword");
                //creates the email
                if (!etEmail.getEditText().getText().toString().isEmpty() && !etPassword.getEditText().getText().toString().isEmpty()) {
                    createNewUser();

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    //creates the user
    void createNewUser() throws Exception {

        RequestParams params = new RequestParams();

        //create a JSON user
        JSONObject user = new JSONObject();
        user.put("first_name", etFirstName.getEditText().getText().toString());
        user.put("middle_name", etMiddleName.getEditText().getText().toString());
        user.put("last_name", etLastName.getEditText().getText().toString());
        user.put("email", etEmail.getEditText().getText().toString());
        user.put("password", etPassword.getEditText().getText().toString());

        //Add to SharedPreference the email and access token and persist == 0 (default)

        StringEntity entity = new StringEntity(user.toString());

        //post the user
        User.registerUser(this, entity, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i(TAG, "the status code for this request is: " + statusCode);
                Toast.makeText(RegisterActivity.this, "Successfully Created Account", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, "the status code for this request is" + statusCode);
                Toast.makeText(RegisterActivity.this, "Error creating Account. Email already been used. Please enter a new one.", Toast.LENGTH_LONG).show();
            }
        });
    }


}