package com.example.findteam_android_v10.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.findteam_android_v10.R;
import com.example.findteam_android_v10.classes.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

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

        resetBtn.setOnClickListener(view1 -> {
            String email = forgot_email.getText().toString();

            if (email.isEmpty()) {
                Toast.makeText(getContext(), "Invalid Email Address", Toast.LENGTH_SHORT).show();

            } else {

                User.sendResetEmail(email);

                FragForgotEmailDirections.ActionMeForgetEmailToMeSentEmail action = FragForgotEmailDirections.actionMeForgetEmailToMeSentEmail(email);
                Navigation.findNavController(view1).navigate(action);

            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}