package com.example.findteam_android_v10.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.findteam_android_v10.LoginActivity;
import com.example.findteam_android_v10.R;

import androidx.fragment.app.Fragment;


public class FragForgotEmailSentSuccess extends Fragment {

    private static final String TAG = "FragForgotEmailSentSuccess";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.frag_forgot_email_sent_success, container, false);
        TextView emailOfUser = view.findViewById(R.id.emailOfUser);
        Button loginBtn = view.findViewById(R.id.emailSuccessBtn);

        FragForgotEmailSentSuccessArgs args = FragForgotEmailSentSuccessArgs.fromBundle(getArguments());
        emailOfUser.setText(args.getEmailOfUser());

        loginBtn.setOnClickListener(v -> {

            Intent i = new Intent(getActivity(), LoginActivity.class);
            startActivity(i);

        });

        return view;
    }
}