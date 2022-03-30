package com.example.findteam_android_v10.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.findteam_android_v10.LoginActivity;
import com.example.findteam_android_v10.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import org.json.JSONException;

import java.util.Arrays;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;

public class FragMyProfile extends Fragment {

    public static final String TAG = "FragMyProfile";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_my_profile, container, false);

        //Elements of the my profile
        TextView fullName;
        fullName = view.findViewById(R.id.profFullName);

        try {
            Log.i(TAG, LoginActivity.currentUser.getString("email"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Toolbar toolbar = view.findViewById(R.id.detail_toolbar);
        //((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);


        List<String> location = Arrays.asList("Houston", "California", "New York");
        List<String> skills = Arrays.asList("C#", "Python", "Java", "Software Developer");
        TagContainerLayout skillsTag = view.findViewById(R.id.skills_tag);
        TagContainerLayout locationTag = view.findViewById(R.id.location_tag);

        Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.questrial);
        locationTag.setTagTypeface(typeface);

        skillsTag.setTags(skills);
        locationTag.setTags(location);

        CollapsingToolbarLayout collapsingToolbarLayout = view.findViewById(R.id.toolbar_layout);
        AppBarLayout appBarLayout = (AppBarLayout) view.findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle("My Profile");
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");//careful there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu, menu);
    }
}
