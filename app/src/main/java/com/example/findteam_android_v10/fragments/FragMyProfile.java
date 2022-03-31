package com.example.findteam_android_v10.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;
import cz.msebera.android.httpclient.Header;

public class FragMyProfile extends Fragment {

    public static final String TAG = "FragMyProfile";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_my_profile, container, false);

        updateUser();

        //Elements of the my profile
        TextView fullName = view.findViewById(R.id.profFullName);
        ImageView ivProfilePic = view.findViewById(R.id.myProfPic);
        FloatingActionButton fab = view.findViewById(R.id.fab);

        List<String> urls = new ArrayList<>(),
                locations = new ArrayList<>(),
                skills = new ArrayList<>();
        TagContainerLayout skillsTag = view.findViewById(R.id.skills_tag);
        TagContainerLayout locationTag = view.findViewById(R.id.location_tag);
        CollapsingToolbarLayout collapsingToolbarLayout = view.findViewById(R.id.toolbar_layout);
        AppBarLayout appBarLayout = (AppBarLayout) view.findViewById(R.id.app_bar);


        //setup recyclerview and adapter
        RecyclerView rvUrl = view.findViewById(R.id.rvUrls);
        urlAdapter urlAdapter = new urlAdapter(getContext(), urls);

        rvUrl.setAdapter(urlAdapter);
        rvUrl.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUrl.addItemDecoration(new DividerItemDecoration(rvUrl.getContext(), LinearLayoutManager.VERTICAL));

        try {

            //set the name in the my profile
            StringBuilder sb = new StringBuilder();
            sb.append(LoginActivity.currentUser.getString("first_name")).append(" ")
                    .append(LoginActivity.currentUser.getString("middle_name")).append(" ")
                    .append(LoginActivity.currentUser.getString("last_name"));
            fullName.setText(sb.toString());

            JSONArray urlsJson = LoginActivity.currentUser.getJSONArray("urls"),
                    tagsJson = LoginActivity.currentUser.getJSONArray("tags");


            //get every url in the user
            for(int i = 0; i < urlsJson.length(); i++){
                JSONObject urlObj = (JSONObject) urlsJson.get(i);
                urls.add(urlObj.getString("domain") + urlObj.getString("path"));
            }
            urlAdapter.notifyDataSetChanged();

            for(int i = 0; i < tagsJson.length(); i++){
                JSONObject tag = (JSONObject) tagsJson.get(i);
                if(tag.getString("category").equals("Location")){
                    locations.add(tag.getString("text"));
                } else if(tag.getString("category").equals("Skill")){
                    skills.add(tag.getString("text"));
                }
            }

            //update the tag container
            skillsTag.setTags(skills);
            locationTag.setTags(locations);

            //update the profile picture
            Glide.with(getContext())
                    .load("https://findteam.2labz.com/picture/" + LoginActivity.currentUser.getString("picture"))
                    .into(ivProfilePic);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.questrial);
        locationTag.setTagTypeface(typeface);
        skillsTag.setTagTypeface(typeface);

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



        return view;
    }

    //always update when it loads the fragment
    private void updateUser() {

        FindTeamClient.get("user", new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                LoginActivity.currentUser = response;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, statusCode + " " + responseString + " " + throwable);
            }
        });

    }

    ActivityResultLauncher<Intent> editProfileActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();

                        //check to see if the user finished updating the profile
                        boolean isFinished = data.getBooleanExtra("finished", false);

                        if(isFinished){
                            Log.i(TAG, "It finished on EditProfileActivity");
                            //update the user
                            //update the fields in the my profile
                        }

                        //else, we ignore


                    }
                }
            }
    );

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Log.i(TAG, "It went here!");
        //checks to see if the user is persisted throughout the app, go login directly
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FindTeam", MODE_PRIVATE);
        //temporarily store login access_token
        sharedPreferences.edit().putString("access_token", "").apply();
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
