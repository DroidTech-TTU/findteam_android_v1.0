package com.example.findteam_android_v10.adapters;

import com.example.findteam_android_v10.fragments.FragFindProjects;
import com.example.findteam_android_v10.fragments.FragFindUsers;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private String testString;

    public ViewPagerAdapter(FragmentManager fragment, Lifecycle lifecycle){
        super(fragment, lifecycle);
        this.testString = testString;
    }

    @NonNull
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