package com.example.findteam_android_v10.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.findteam_android_v10.EditProfileActivity;
import com.example.findteam_android_v10.FindTeamClient;
import com.example.findteam_android_v10.LoginActivity;
import com.example.findteam_android_v10.R;
import com.example.findteam_android_v10.adapters.urlAdapter;
import com.example.findteam_android_v10.classes.Project;
import com.example.findteam_android_v10.classes.User;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;
import cz.msebera.android.httpclient.Header;

public class FragMyProfile extends Fragment {

    public static final String TAG = "FragMyProfile";

    TextView fullName;
    ImageView ivProfilePic;
    List<String> urls, locations, skills;
    urlAdapter urlAdapter;
    TagContainerLayout skillsTag, locationTag;
    FloatingActionButton fab;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_my_profile, container, false);

        //Elements of the my profile
        fullName = view.findViewById(R.id.profFullName);
        ivProfilePic = view.findViewById(R.id.myProfPic);

        TextView finishedProj = view.findViewById(R.id.finished_project_count),
                activeProj = view.findViewById(R.id.active_project_count);

        fab = view.findViewById(R.id.fab);

        urls = new ArrayList<>();
        locations = new ArrayList<>();
        skills = new ArrayList<>();

        skillsTag = view.findViewById(R.id.skills_tag);
        locationTag = view.findViewById(R.id.location_tag);

        CollapsingToolbarLayout collapsingToolbarLayout = view.findViewById(R.id.toolbar_layout);
        AppBarLayout appBarLayout = (AppBarLayout) view.findViewById(R.id.app_bar);

        //setup recyclerview and adapter
        RecyclerView rvUrl = view.findViewById(R.id.rvUrls);
        urlAdapter = new urlAdapter(getContext(), urls);

        rvUrl.setAdapter(urlAdapter);
        rvUrl.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUrl.addItemDecoration(new DividerItemDecoration(rvUrl.getContext(), LinearLayoutManager.VERTICAL));

        Toolbar toolbar = view.findViewById(R.id.detail_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

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
                    collapsingToolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    isShow = false;
                }
            }
        });

        //if user selected to edit the profile
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getContext(), EditProfileActivity.class);
                editProfileActivityResultLauncher.launch(i);
            }
        });

        User.getCurrentUser(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                LoginActivity.currentUser = response;

                Project.getMyProjects(new JsonHttpResponseHandler(){
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


                loadProfile(false, LoginActivity.currentUser);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("FindTeam", Context.MODE_PRIVATE);
                sharedPreferences.edit().putString("access_token","").apply();
                Toast.makeText(getContext(), "Cannot fetch data. Please re-login again.", Toast.LENGTH_LONG).show();
            }
        });



        return view;
    }

    private void loadProfile(Boolean update, JSONObject user) {

        if(update){
            urls.clear();
            locations.clear();
            skills.clear();
        }

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

            Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.questrial);
            locationTag.setTagTypeface(typeface);
            skillsTag.setTagTypeface(typeface);

            //update the tag container
            skillsTag.setTags(skills);
            locationTag.setTags(locations);

            //update the profile picture
            Glide.with(getContext())
                    .load("https://findteam.2labz.com/picture/" + user.getString("picture"))
                    .into(ivProfilePic);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    ActivityResultLauncher<Intent> editProfileActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if(result.getResultCode() == 200){

                        Intent data = result.getData();
                        User.getCurrentUser();
                        Log.i(TAG, "It finished on EditProfileActivity");

                        loadProfile(true, LoginActivity.currentUser);

                    }

                    //else, we ignore
                }
            }
    );

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        //temporarily store login access_token
        LoginActivity.sharedPreferences.edit().putString("access_token", "").apply();
        Intent i = new Intent(getContext(), LoginActivity.class);
        startActivity(i);
        getActivity().finish();
        return false;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
