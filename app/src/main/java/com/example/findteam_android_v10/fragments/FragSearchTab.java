package com.example.findteam_android_v10.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.findteam_android_v10.R;
import com.example.findteam_android_v10.adapters.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

public class FragSearchTab extends Fragment {

    public static final String TAG = "FragSearchTab";
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private EditText searchText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_search_tabs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // find views by id
        viewPager2 = view.findViewById(R.id.viewpager);
        tabLayout = view.findViewById(R.id.tablayout);
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

        ImageButton searchBtn = view.findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (viewPager2.getCurrentItem() == 0) {
                    FragFindProjects searchFrag = (FragFindProjects) getActivity().getSupportFragmentManager().findFragmentByTag("f" + viewPager2.getCurrentItem());
                    try {
                        searchFrag.search(searchText.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    FragFindUsers fragUser = (FragFindUsers) getActivity().getSupportFragmentManager().findFragmentByTag("f1");
                    fragUser.search(searchText.getText().toString());
                }

            }
        });

    }
}
