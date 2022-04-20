package com.example.findteam_android_v10;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.findteam_android_v10.adapters.urlAdapter;
import com.example.findteam_android_v10.fragments.FragChatHistory;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;

public class MemberProfileActivity extends AppCompatActivity {

    public static final String TAG = "MemberProfileActivity";

    TextView fullName;
    ImageView ivProfilePic;
    List<String> urls, locations, skills;
    com.example.findteam_android_v10.adapters.urlAdapter urlAdapter;
    TagContainerLayout skillsTag, locationTag;
    FloatingActionButton fab;
    JSONObject user;
    FloatingActionButton btChatMemberProfile;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_profile);
        context = this;
        String fullMemName = getIntent().getStringExtra("fullname");
        try {
            user = new JSONObject(getIntent().getStringExtra("user"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Elements of the my profile
        fullName = findViewById(R.id.profFullName);
        ivProfilePic = findViewById(R.id.myProfPic);

        fab = findViewById(R.id.fab);

        urls = new ArrayList<>();
        locations = new ArrayList<>();
        skills = new ArrayList<>();

        skillsTag = findViewById(R.id.skills_tag);
        locationTag = findViewById(R.id.tagContainer);

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);

        //setup recyclerview and adapter
        RecyclerView rvUrl = findViewById(R.id.rvUrls);
        urlAdapter = new urlAdapter(this, urls);

        rvUrl.setAdapter(urlAdapter);
        rvUrl.setLayoutManager(new LinearLayoutManager(this));
        rvUrl.addItemDecoration(new DividerItemDecoration(rvUrl.getContext(), LinearLayoutManager.VERTICAL));

        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        btChatMemberProfile = findViewById(R.id.btChatMemberProfile);
        btChatMemberProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent i = new Intent(context, MainActivity.class);
                try {
                    i.putExtra("puid", user.getInt("uid"));
                    i.putExtra("is_user", true);
                    i.putExtra("fullName", fullMemName);
                    i.putExtra("request", MainActivity.REQUEST_CHAT_HISTORY);
                    startActivity(i);
                } catch (JSONException exception) {
                    exception.printStackTrace();
                }

            }
        });

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(fullMemName);
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");//careful there should a space between double quote otherwise it wont work
                    collapsingToolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(MemberProfileActivity.this, R.color.white));
                    isShow = false;
                }
            }
        });


        loadProfile(user);



    }

    protected void loadProfile(JSONObject user) {

        try {

            //set the name in the my profile
            StringBuilder sb = new StringBuilder();
            sb.append(user.getString("first_name")).append(" ")
                    .append(user.getString("middle_name")).append(" ")
                    .append(user.getString("last_name"));
            fullName.setText(sb.toString());

            JSONArray urlsJson = user.getJSONArray("urls"),
                    tagsJson = user.getJSONArray("tags");

            //get every url in the user
            for(int i = 0; i < urlsJson.length(); i++){
                JSONObject urlObj = (JSONObject) urlsJson.get(i);
                urls.add(urlObj.getString("domain") + urlObj.getString("path"));
                Log.i(TAG, urls.get(i));
            }

            urlAdapter.notifyDataSetChanged();

            for(int i = 0; i < tagsJson.length(); i++){
                JSONObject tag = (JSONObject) tagsJson.get(i);
                if(tag.getString("category").equals("Location")){
                    locations.add(tag.getString("text"));
                } else if(tag.getString("category").equals("Skills")){
                    skills.add(tag.getString("text"));
                }
            }

            Typeface typeface = ResourcesCompat.getFont(this, R.font.questrial);
            locationTag.setTagTypeface(typeface);
            skillsTag.setTagTypeface(typeface);

            //update the tag container
            skillsTag.setTags(skills);
            locationTag.setTags(locations);

            //update the profile picture
            Glide.with(this)
                    .load("https://findteam.2labz.com/picture/" + user.getString("picture"))
                    .into(ivProfilePic);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}