package com.example.findteam_android_v10.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.example.findteam_android_v10.LoginActivity;
import com.example.findteam_android_v10.R;

public class FragForgotResetSuccess extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_forgot_reset_success, container, false);

        Button successBtn = view.findViewById(R.id.successBtn);
        ImageView password_success_icon = view.findViewById(R.id.password_success_icon);

        Animation animFadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fadein);
        password_success_icon.startAnimation(animFadeIn);

        successBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), LoginActivity.class);
                startActivity(i);
            }
        });
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}