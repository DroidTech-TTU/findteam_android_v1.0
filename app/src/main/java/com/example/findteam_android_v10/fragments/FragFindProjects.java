package com.example.findteam_android_v10.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.findteam_android_v10.R;
import com.example.findteam_android_v10.adapters.SearchTabAdapter;
import com.google.android.material.tabs.TabLayout;

public class FragFindProjects extends Fragment {

    public static final String TAG = "FragFindProjects";
    TabLayout tabLayout;
    ViewPager2 viewPager;
    SearchTabAdapter searchTabAdapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_find_projects, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        tabLayout= view.findViewById(R.id.tabLayout);
//        viewPager= view.findViewById(R.id.viewPager);
//
//        tabLayout.addTab(tabLayout.newTab().setText("Home"));
//        tabLayout.addTab(tabLayout.newTab().setText("Sport"));
//        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
//        searchTabAdapter = new SearchTabAdapter(view.getContext(), getFragmentManager(), 2, getLifecycle());
//        viewPager.setAdapter(searchTabAdapter);
//
//
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                viewPager.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
    }
}
