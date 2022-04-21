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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.findteam_android_v10.adapters.ProfileTagAdapter;
import com.example.findteam_android_v10.adapters.urlAdapter;
import com.example.findteam_android_v10.classes.Project;
import com.example.findteam_android_v10.classes.User;
import com.example.findteam_android_v10.fragments.FragChatHistory;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;
import cz.msebera.android.httpclient.Header;

public class MemberProfileActivity extends AppCompatActivity {

    public static final String TAG = "MemberProfileActivity";

    TextView fullName;
    ImageView ivProfilePic;
    List<String> urls, categories;
    List<List<String>> tags;

    urlAdapter urlAdapter;
    ProfileTagAdapter profileTagAdapter;

    FloatingActionButton goBackFab;
    JSONObject user;
    FloatingActionButton btChatMemberProfile;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_profile);
        context = this;
        String fullMemName = getIntent().getStringExtra("fullname");

        TextView finishedProj = findViewById(R.id.finished_project_count),
                activeProj = findViewById(R.id.active_project_count);

        try {
            user = new JSONObject(getIntent().getStringExtra("user"));
            Log.i(TAG, user.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Elements of the my profile
        fullName = findViewById(R.id.profFullName);
        ivProfilePic = findViewById(R.id.myProfPic);

        goBackFab = findViewById(R.id.goBackFab);

        urls = new ArrayList<>();
        categories = new ArrayList<>();
        tags = new ArrayList<>();

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);

        //setup url adapter
        RecyclerView rvUrl = findViewById(R.id.rvUrls);
        urlAdapter = new urlAdapter(this, urls);
        rvUrl.setAdapter(urlAdapter);
        rvUrl.setLayoutManager(new LinearLayoutManager(this));
        rvUrl.addItemDecoration(new DividerItemDecoration(rvUrl.getContext(), LinearLayoutManager.VERTICAL));

        //setup recyclerview and adapter for tags
        RecyclerView rvTags = findViewById(R.id.rvProfileTags);
        profileTagAdapter = new ProfileTagAdapter(this, categories, tags);
        rvTags.setAdapter(profileTagAdapter);
        rvTags.setLayoutManager(new LinearLayoutManager(this));

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

        goBackFab.setOnClickListener(view -> {
            finish();
        });


        //getting the number of projects for active and finished
        try {
            Project.getMyProjects(user.getInt("uid"), new JsonHttpResponseHandler(){

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    int finished = 0, active = 0;
                    for(int i = 0; i < response.length(); i++){
                        try {
                            if(((JSONObject)response.get(i)).getInt("status") == 0)
                                active++;
                            else if(((JSONObject)response.get(i)).getInt("status") == 2)
                                finished++;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    //get the finished project and active project count
                    finishedProj.setText(String.valueOf(finished));
                    activeProj.setText(String.valueOf(active));
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.i(TAG, throwable + " " + errorResponse);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

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

            //load the tags of the user
            for(int i = 0; i < tagsJson.length(); i++){
                JSONObject tagObj = (JSONObject) tagsJson.get(i);
                if(!categories.contains(tagObj.getString("category"))){
                    categories.add(tagObj.getString("category"));
                }

            }

            for(int i = 0; i < categories.size(); i++){
                List<String> localTags = new ArrayList<>();
                for(int j = 0; j < tagsJson.length(); j++){
                    JSONObject tagObj = (JSONObject) tagsJson.get(j);
                    if(categories.get(i).equals(tagObj.getString("category"))){
                        localTags.add(tagObj.getString("text"));
                    }
                }
                tags.add(localTags);
            }

            profileTagAdapter.notifyDataSetChanged();



            //update the profile picture
            Glide.with(this)
                    .load("https://findteam.2labz.com/picture/" + user.getString("picture"))
                    .into(ivProfilePic);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}