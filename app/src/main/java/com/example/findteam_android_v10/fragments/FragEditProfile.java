package com.example.findteam_android_v10.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.findteam_android_v10.R;

import java.util.Arrays;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;

public class FragEditProfile extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.frag_edit_profile, container, false);

        List<String> skills = Arrays.asList("C#", "Python", "Java", "Software Developer","C#", "Python", "Java", "Software Developer","C1#", "P1ython", "Ja1va", "Softw1are Developer","Software Dev2eloper","C1#2", "P12ython", "Ja1v2a", "Softw1are De2veloper");
        TagContainerLayout mTagContainerLayout = view.findViewById(R.id.skills_tag);
        mTagContainerLayout.setTags(skills);

        return view;
    }
}