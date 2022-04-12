package com.example.findteam_android_v10.adapters;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.findteam_android_v10.fragments.FragFindProjects;
import com.example.findteam_android_v10.fragments.FragFindUsers;

public class ViewPagerAdapter extends FragmentStateAdapter {

    String testString;
    public ViewPagerAdapter(FragmentManager fragment, Lifecycle lifecycle){
        super(fragment, lifecycle);
        this.testString = testString;
    }

    public Fragment createFragment(int position) {
        if (position == 1) {
            return new FragFindUsers();
        }
        return new FragFindProjects();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}