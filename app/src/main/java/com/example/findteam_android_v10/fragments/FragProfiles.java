package com.example.findteam_android_v10.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.findteam_android_v10.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;

public class FragProfiles extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_profile, container, false);

        List<String> skills = Arrays.asList("C#", "Python", "Java", "Software Developer","C#", "Python", "Java", "Software Developer","C1#", "P1ython", "Ja1va", "Softw1are Developer","Software Dev2eloper","C1#2", "P12ython", "Ja1v2a", "Softw1are De2veloper");
        TagContainerLayout mTagContainerLayout = view.findViewById(R.id.skills_tag);
        mTagContainerLayout.setTags(skills);

        return view;
    }
}
