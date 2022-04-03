package com.example.findteam_android_v10;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.findteam_android_v10.adapters.DetailMyProjectAdapter;
import com.example.findteam_android_v10.adapters.GalleryCreateProjectAdapter;
import com.example.findteam_android_v10.classes.Picture;
import com.example.findteam_android_v10.classes.Project;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.Policy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;
import cz.msebera.android.httpclient.Header;

public class DetailMyProjectActivity extends AppCompatActivity {
    public static final int EDIT_PROJECT_CODE = 1123;
    public static final String TAG = "DetailMyProjectActivity";
    RecyclerView rvMembers;
    Context context;
    TextView tvDescription;
    TextView tvProjectTitleDetailProject;
    TextView tvProjectStatus;
    ImageButton ibEditProject;
    ImageButton ibDeleteProject;
    ImageView ivStatus;
    JSONObject project;
    JSONArray members;
    TagContainerLayout tgDetailProject;
    DetailMyProjectAdapter detailMyProjectAdapter;

    List<String> picturesURLs;
    List<Bitmap> pictureFiles;
    GalleryCreateProjectAdapter adapter;
    RecyclerView rvGallery;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_detail_my_project);
        pictureFiles = new ArrayList<>();
        rvGallery = findViewById(R.id.rvGalleryDetailProject);
        this.rvMembers = (RecyclerView) findViewById(R.id.rvMembers);
        this.tvDescription = (TextView) findViewById(R.id.tvDescriptionMyDetailProjects);
        this.tvProjectStatus = (TextView) findViewById(R.id.tvStatusMyProjectDetailMain);
        this.ivStatus = (ImageView) findViewById(R.id.ivStatusMyProjectDetail) ;
        this.tvProjectTitleDetailProject = findViewById(R.id.tvProjecTitleDetailProject);
        this.tgDetailProject = findViewById(R.id.tgDetailProject);
        this.ibEditProject= findViewById(R.id.ibEditProject);
        this.ibDeleteProject= findViewById(R.id.ibDeleteProject);
        int pid = getIntent().getIntExtra("pid", -1);
        String URL = Project.GET_PROJECT_API_URL + pid;
        FindTeamClient.get(URL, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                project = response;
                try {

                    if(!isOwner(project.getInt("owner_uid"))){
                        ibEditProject.setVisibility(View.INVISIBLE);
                        ibDeleteProject.setVisibility(View.INVISIBLE);
                    }else{
                        ibEditProject.setVisibility(View.VISIBLE);
                        ibDeleteProject.setVisibility(View.VISIBLE);
                        ibDeleteProject.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d(TAG, "On Delete Project Button");
                            }
                        });
                        ibEditProject.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(context, UpdateProjectActivity.class);
                                i.putExtra("project", project.toString());
                                startActivityForResult(i, EDIT_PROJECT_CODE);
                                Log.d(TAG, "On Edit Project Button");
                            }
                        });
                    }

                    //Get Picture URLS
                    picturesURLs = new ArrayList<>();
                    picturesURLs = Project.getPictures(project);

                    adapter = new GalleryCreateProjectAdapter(context, picturesURLs);
                    // Attach the adapter to the recyclerview to populate items
                    rvGallery.setAdapter(adapter);
                    // Set layout manager to position the items
                    rvGallery.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));

                    Log.d(TAG, "On Create: " + project.toString());
                    tvProjectTitleDetailProject.setText(project.getString("title"));
                    tvDescription.setText(project.getString("description"));
                    updateImageStatus(project.getInt("status"));
                    List<String> tags = Project.getTagsList(project);
                    tgDetailProject.setTags(tags);
                    members = project.getJSONArray("members");
                    detailMyProjectAdapter = new DetailMyProjectAdapter(context, project.getJSONArray("members"));
                    rvMembers.setAdapter(detailMyProjectAdapter);
                    rvMembers.setLayoutManager(new LinearLayoutManager(context));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "On Create: " + project.toString());
                Log.i(TAG, "the status code for this request is: " + statusCode);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null && resultCode == EDIT_PROJECT_CODE){
            FindTeamClient.get(Project.getURLGetProject(data.getIntExtra("pid", -1)), new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    try {
                        JSONObject resultProject = new JSONObject(new String(responseBody));
                        Log.d(TAG, "On Activity Result: result = " + resultProject);
                        tvProjectTitleDetailProject.setText(resultProject.getString("title"));
                        tvDescription.setText(resultProject.getString("description"));
                        updateImageStatus(resultProject.getInt("status"));
                        List<String> tags = Project.getTagsList(resultProject);
                        tgDetailProject.setTags(tags);

                        adapter.clear();
                        adapter.addAll(Project.getPictures(resultProject));

                        members = resultProject.getJSONArray("members");
                        detailMyProjectAdapter.clear();
                        detailMyProjectAdapter.addAll(members);
                        rvMembers.setAdapter(detailMyProjectAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.e(TAG, "Get Project Fail" + statusCode);
                    Toast.makeText(context, "Failure to Get project", Toast.LENGTH_LONG).show();
                }
            });

        }

    }

    public void deleteProject(int pid){
        //Delete Project
        FindTeamClient.post(Project.getURLDeleteProject(pid), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(TAG, "On Success DeleteProject: " + statusCode);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(TAG, "On Failure DeleteProject: " + statusCode);
            }
        });
    }
    public boolean isOwner(int owner_id) throws JSONException {

        return LoginActivity.currentUser.getInt("uid") == owner_id;
    }

    public void updateImageStatus(int i){
        Log.d(TAG, "Status ID = " + i);
        switch (i){
            case 0:{
                tvProjectStatus.setText(Project.STATUS_IN_PROGRESS_STRING);
                ivStatus.setImageResource(R.drawable.ic_project_status_in_progress_green);
                break;
            }
            case 1:{
                tvProjectStatus.setText(Project.STATUS_PENDING_STRING);
                ivStatus.setImageResource(R.drawable.ic_project_status_in_pending_green);
                break;
            }
            case 2:{
                tvProjectStatus.setText(Project.STATUS_FINISHED_STRING);
                ivStatus.setImageResource(R.drawable.ic_project_status_in_finished_green); break;
            }
        }
    }

}