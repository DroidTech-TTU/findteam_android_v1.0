package com.example.findteam_android_v10.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.findteam_android_v10.R;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.Arrays;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;

public class FragMyProfile extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_my_profile, container, false);
        Button changeProfile = view.findViewById(R.id.change_profile);


        List<String> location = Arrays.asList("Houston", "California", "New York");
        List<String> skills = Arrays.asList("C#", "Python", "Java", "Software Developer");
        TagContainerLayout skillsTag = view.findViewById(R.id.skills_tag);
        TagContainerLayout locationTag = view.findViewById(R.id.location_tag);

        skillsTag.setTags(skills);
        locationTag.setTags(location);

        return view;
    }
}
