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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.findteam_android_v10.adapters.DetailMyProjectAdapter;
import com.example.findteam_android_v10.adapters.GalleryCreateProjectAdapter;
import com.example.findteam_android_v10.classes.Picture;
import com.example.findteam_android_v10.classes.Project;
import com.example.findteam_android_v10.fragments.FragMyProjects;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.Policy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

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
    ImageButton ibGoBack;
    Button btJoinProject, btLeaveProject;
    List<String> picturesURLs;
    List<Bitmap> pictureFiles;
    GalleryCreateProjectAdapter adapter;
    RecyclerView rvGallery;
    PopupWindow popupWindow;
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
        this.ibGoBack = findViewById(R.id.ibGoBack);
        ibGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        this.btJoinProject = findViewById(R.id.btJoinProject);
        this.btLeaveProject = findViewById(R.id.btLeaveProject);
        try {
            project = new JSONObject(getIntent().getStringExtra("project"));
            int memType = Project.getUserMembershipType(LoginActivity.currentUser.getInt("uid"), project);
            if(LoginActivity.currentUser.getInt("uid") == project.getInt("owner_uid")) memType = Project.MEMBER_SHIP__TYPE_OWNER;
            Log.d(TAG, "Membership_type: " + memType);
            switch (memType){
                case Project.MEMBER_SHIP__TYPE_OWNER:{
                    Log.d(TAG, "Owner!!!");
                    ibEditProject.setVisibility(View.VISIBLE);
                    ibDeleteProject.setVisibility(View.VISIBLE);
                    btJoinProject.setVisibility(View.INVISIBLE);
                    btLeaveProject.setVisibility(View.INVISIBLE);
                    ibDeleteProject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                onButtonDeletePopupWindowClick(v, project.getInt("pid"));
                            } catch (JSONException exception) {
                                exception.printStackTrace();
                            }
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
                    break;
                }
                case Project.MEMBER_SHIP__TYPE_PENDING:
                case Project.MEMBER_SHIP__TYPE_MEMBER: {
                    Log.d(TAG, "PENDING | MEMBER");
                    ibEditProject.setVisibility(View.INVISIBLE);
                    ibDeleteProject.setVisibility(View.INVISIBLE);
                    btJoinProject.setVisibility(View.INVISIBLE);
                    btLeaveProject.setVisibility(View.INVISIBLE);
                    break;
                }
                case Project.MEMBER_SHIP__TYPE_GUEST:{
                    ibEditProject.setVisibility(View.INVISIBLE);
                    ibDeleteProject.setVisibility(View.INVISIBLE);
                    btJoinProject.setVisibility(View.VISIBLE);
                    btJoinProject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                joinProject();
                            } catch (JSONException exception) {
                                exception.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    break;
                }
            }

            try {

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
                detailMyProjectAdapter = new DetailMyProjectAdapter(context, project.getJSONArray("members"), project);
                rvMembers.setAdapter(detailMyProjectAdapter);
                rvMembers.setLayoutManager(new LinearLayoutManager(context));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "On Create: " + project.toString());

        } catch (JSONException exception) {
            exception.printStackTrace();
        }
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
    public void deleteProject(int pid) throws JSONException, UnsupportedEncodingException {
        //Delete Project


        RequestParams params = new RequestParams();
        FindTeamClient.delete(Project.getURLDeleteProject(pid), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(TAG, "On Success DeleteProject: " + statusCode);

                try {
                    Intent i = new Intent();
                    i.putExtra("pid", project.getInt("pid"));
                    setResult(FragMyProjects.DELETE_PROJECT_CODE, i);
                    finish();
                } catch (JSONException exception) {
                    exception.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(TAG, "On Failure DeleteProject: " + statusCode);
                Log.d(TAG, "On Failure DeleteProject: " + new String(responseBody));
            }
        });
    }
    public boolean isOwner(int owner_id) throws JSONException {

        return LoginActivity.currentUser.getInt("uid") == owner_id;
    }
    public void onButtonDeletePopupWindowClick(View view, int pid) {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_cancel_delete_project, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
       popupWindow = new PopupWindow(popupView,width,height,focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        Button btYesCancelProject = popupView.findViewById(R.id.btYesCancelProject);
        Button btNoCancelProject = popupView.findViewById(R.id.btNoCancelProject);

        btYesCancelProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                try {
                    try {
                        deleteProject(pid);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException exception) {
                    exception.printStackTrace();
                }
            }
        });
        btNoCancelProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    private void joinProject() throws JSONException, UnsupportedEncodingException {

        JSONObject member = new JSONObject();
        member.put("uid", LoginActivity.currentUser.get("uid"));
        member.put("membership_type", Project.MEMBER_SHIP__TYPE_PENDING);
        JSONArray members = project.getJSONArray("members");
        members.put(member);

        Log.d(TAG, project.toString());
        int tmpPid = project.getInt("pid");
        FindTeamClient.post(Project.getURLJoinProject(tmpPid), new AsyncHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                btJoinProject.setVisibility(View.INVISIBLE);
                detailMyProjectAdapter.addTail(members);
                Log.i(TAG, "JoinProject(): the status code for this request is: " + statusCode);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(TAG, "JoinProject(): project: " + new String(responseBody));
                Log.e(TAG, "JoinProject(): the status code for this request is" + statusCode);
            }

        });
    }

    private void leaveProject() throws JSONException, UnsupportedEncodingException {

        JSONObject member = new JSONObject();
        member.put("uid", LoginActivity.currentUser.get("uid"));
        member.put("membership_type", Project.MEMBER_SHIP__TYPE_GUEST);
        JSONArray members = project.getJSONArray("members");
        for(int i=0; i<members.length(); i++){
            if(LoginActivity.currentUser.getInt("uid") == members.getJSONObject(i).getInt("uid")){
                members.remove(i);
            }
        }

        Log.d(TAG, project.toString());
        String URL = Project.getURLUpdateProject(project.getInt("pid"));
        int tmpPid = project.getInt("pid");
        JSONArray tmpPics = project.getJSONArray("pictures");
        int ownerId = project.getInt("owner_uid");

        project.remove("pid");
        project.remove("pictures");
        project.remove("owner_uid");

        Log.d(TAG, "PostData: " + project.toString());
        Log.d(TAG, "Picture: " + tmpPics.toString());
        StringEntity entity = new StringEntity(project.toString());

        project.put("pid", tmpPid);
        project.put("pictures", tmpPics);
        project.put("owner_uid", ownerId);

        FindTeamClient.post(this,URL, entity, new AsyncHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i(TAG, "saveProject(): the status code for this request is: " + statusCode);
                Toast.makeText(context, "Successfully Join Project", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(TAG, "saveProject(): project: " + project.toString());
                Log.e(TAG, "saveProject(): the status code for this request is" + statusCode);
                Toast.makeText(context, "Failure to Join project", Toast.LENGTH_LONG).show();
            }

        });
    }
}