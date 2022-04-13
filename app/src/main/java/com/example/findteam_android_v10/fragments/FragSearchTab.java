package com.example.findteam_android_v10.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.findteam_android_v10.R;
import com.example.findteam_android_v10.adapters.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;

public class FragSearchTab extends Fragment {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ImageButton searchBtn;
    EditText searchText;
    public static final String TAG = "FragSearchTab";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.frag_search_tabs, container, false);
        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // find views by id
        viewPager2 = view.findViewById(R.id.viewpager);
        tabLayout = view.findViewById(R.id.tablayout);
        searchBtn = view.findViewById(R.id.searchBtn);
        searchText = view.findViewById(R.id.etSearchBar);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), getLifecycle());

        tabLayout.addTab(tabLayout.newTab().setText("Projects"));
        tabLayout.addTab(tabLayout.newTab().setText("Users"));

        viewPager2.setAdapter(viewPagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.getTabAt(position).select();
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(viewPager2.getCurrentItem() == 0){
                    FragFindProjects searchFrag = (FragFindProjects) getActivity().getSupportFragmentManager().findFragmentByTag("f" + viewPager2.getCurrentItem());
                    try {
                        searchFrag.search(searchText.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                else{
                    FragFindUsers fragUser = (FragFindUsers) getActivity().getSupportFragmentManager().findFragmentByTag("f1");
                    fragUser.search(searchText.getText().toString());
                }

            }
        });

    }
}
