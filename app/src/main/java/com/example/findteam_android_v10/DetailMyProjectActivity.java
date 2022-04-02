package com.example.findteam_android_v10;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.findteam_android_v10.adapters.DetailMyProjectAdapter;
import com.example.findteam_android_v10.classes.Project;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.Policy;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;
import cz.msebera.android.httpclient.Header;

public class DetailMyProjectActivity extends AppCompatActivity {
    public static final int EDIT_PROJECT_CODE = 1123;
    public static final String GET_PROJECT_API_URL = "project?pid=";
    public static final String STATUS_IN_PROGRESS_STRING = "In Progress";
    public static final int STATUS_IN_PROGRESS_INT = 0;
    public static final String STATUS_FINISHED_STRING = "Finished";
    public static final int STATUS_IN_FINISHED_INT = 1;
    public static final String TAG = "DetailMyProjectActivity";
    RecyclerView rvMembers;
    Context context;
    TextView tvDescription;
    TextView tvProjecTitleDetailProject;
    TextView tvProjectStatus;
    ImageButton ibEditProject;
    ImageButton ibDeleteProject;
    ImageView ivStatus;
    JSONObject project;
    JSONArray members;
    TagContainerLayout tgDetailProject;
    DetailMyProjectAdapter detailMyProjectAdapter;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_detail_my_project);
        this.rvMembers = (RecyclerView) findViewById(R.id.rvMembers);
        this.tvDescription = (TextView) findViewById(R.id.tvDescriptionMyDetailProjects);
        this.tvProjectStatus = (TextView) findViewById(R.id.tvStatusMyProjectDetailMain);
        this.ivStatus = (ImageView) findViewById(R.id.ivStatusMyProjectDetail) ;
        this.tvProjecTitleDetailProject = findViewById(R.id.tvProjecTitleDetailProject);
        this.tgDetailProject = findViewById(R.id.tgDetailProject);
        this.ibEditProject= findViewById(R.id.ibEditProject);
        this.ibDeleteProject= findViewById(R.id.ibDeleteProject);
        int pid = getIntent().getIntExtra("pid", -1);
        String URL = GET_PROJECT_API_URL + pid;
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
                    Log.d(TAG, "On Create: " + project.toString());
                    tvProjecTitleDetailProject.setText(project.getString("title"));
                    tvDescription.setText(project.getString("description"));
                    String status = project.getInt("status") == STATUS_IN_PROGRESS_INT?STATUS_IN_PROGRESS_STRING:STATUS_FINISHED_STRING;
                    tvProjectStatus.setText(status);
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

            JSONObject updatedProject = null;
            try {
                JSONObject resultProject = new JSONObject(data.getStringExtra("project"));
                Log.d(TAG, "onActivityResult: data = " + resultProject.toString());
                Log.d(TAG, "onActivityResult: resultCode = " + resultCode);

                tvProjecTitleDetailProject.setText(resultProject.getString("title"));
                tvDescription.setText(resultProject.getString("description"));
                String status = resultProject.getInt("status") == STATUS_IN_PROGRESS_INT?STATUS_IN_PROGRESS_STRING:STATUS_FINISHED_STRING;
                tvProjectStatus.setText(status);
                List<String> tags = Project.getTagsList(resultProject);
                tgDetailProject.setTags(tags);


                members = resultProject.getJSONArray("members");
                detailMyProjectAdapter.clear();
                detailMyProjectAdapter.addAll(members);
                rvMembers.setAdapter(detailMyProjectAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }


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

}