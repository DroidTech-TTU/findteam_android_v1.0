package com.example.findteam_android_v10.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.findteam_android_v10.FindTeamClient;
import com.example.findteam_android_v10.R;
import com.example.findteam_android_v10.classes.User;

import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragForgotNewPass extends Fragment {

    private static final String TAG = "FragNewPass";
    private JSONObject user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_forgot_new_pass, container, false);

        Button resetBtn = view.findViewById(R.id.newPassBtn);
        EditText newPass1 = view.findViewById(R.id.newPass1), newPass2 = view.findViewById(R.id.newPass2);

        String accessToken = getArguments().getString("access_token");

        //set the bearer auth
        FindTeamClient.setAuth(accessToken);

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!newPass1.getText().toString().equals(newPass2.getText().toString()))
                    Toast.makeText(getContext(), "Password does not match. Please try again", Toast.LENGTH_LONG).show();
                else if(newPass1.getText().toString().equals("") || newPass2.toString().equals(""))
                    Toast.makeText(getContext(), "Password cannot be empty. Please try again", Toast.LENGTH_LONG).show();
                else
                    User.resetPass(getContext(), view, newPass1.getText().toString());

            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}