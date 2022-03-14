package com.example.findteam_android_v10.fragments;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import com.example.findteam_android_v10.DetailMyProjectActivity;
import com.example.findteam_android_v10.MainActivity;
import com.example.findteam_android_v10.R;
import com.example.findteam_android_v10.RegisterActivity;

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

        Button changeProfile = view.findViewById(R.id.change_profile);

        changeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Navigation.findNavController(view).navigate(R.id.action_meProfile_to_editProfile);
                //getActivity().getFragmentManager().beginTransaction().replace(R.id.activity_main_nav_host_fragment, new BlankFragment()).commit();
                //Intent i = new Intent(view.getContext(), FragChat.class);
                //startActivity(i);
            }
        });
        List<String> skills = Arrays.asList("C#", "Python", "Java", "Software Developer","C#", "Python", "Java", "Software Developer","C1#", "P1ython", "Ja1va", "Softw1are Developer","Software Dev2eloper","C1#2", "P12ython", "Ja1v2a", "Softw1are De2veloper");
        TagContainerLayout mTagContainerLayout = view.findViewById(R.id.skills_tag);
        mTagContainerLayout.setTags(skills);

        return view;
    }
}
