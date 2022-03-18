package com.example.findteam_android_v10.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.example.findteam_android_v10.ForgetActivity;
import com.example.findteam_android_v10.R;

public class FragForgotEmail extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_frag_forgot_email, container, false);

        ForgetActivity forgetActivity = (ForgetActivity) getActivity();
        if (forgetActivity != null && forgetActivity.data != null)
            Navigation.findNavController(view).navigate(R.id.action_meSecurityCode_to_meNewPass);

        Button resetBtn = view.findViewById(R.id.resetBtn);
        ImageView lock_ic = view.findViewById(R.id.lock_ic);

        Animation animFadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fadein);

        lock_ic.startAnimation(animFadeIn);

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Navigation.findNavController(view).navigate(R.id.action_meForgetEmail_to_meSecurityCode);
            }
        });
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}