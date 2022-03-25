package com.example.findteam_android_v10.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.findteam_android_v10.FindTeamClient;
import com.example.findteam_android_v10.R;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class FragForgotEmail extends Fragment {

    public static final String TAG = "FragForgotEmail";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_forgot_email, container, false);

        Button resetBtn = view.findViewById(R.id.resetBtn);
        ImageView lock_ic = view.findViewById(R.id.lock_ic);

        EditText forgot_email = view.findViewById(R.id.forgot_email);

        Animation animFadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fadein);
        lock_ic.startAnimation(animFadeIn);

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = forgot_email.getText().toString();

                if(email.isEmpty()) {
                    Toast.makeText(getContext(), "Invalid Email Address", Toast.LENGTH_SHORT).show();
                }
                else {

                    //add a post statement here
                    RequestParams requestParams = new RequestParams();
                    requestParams.put("email", email);

                    FindTeamClient.post("user/reset", requestParams, new JsonHttpResponseHandler(){

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.i(TAG, "email has been sent successfully");
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.e(TAG, "email has not been sent. Error code: " + statusCode);
                        }
                    });

                    FragForgotEmailDirections.ActionMeForgetEmailToMeSentEmail action = FragForgotEmailDirections.actionMeForgetEmailToMeSentEmail(email);
                    Navigation.findNavController(view).navigate(action);
                }
            }
        });
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}