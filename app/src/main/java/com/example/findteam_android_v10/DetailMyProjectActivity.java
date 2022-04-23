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
import com.example.findteam_android_v10.adapters.ProfileTagAdapter;
import com.example.findteam_android_v10.adapters.ProjectDetailTaglAdapter;
import com.example.findteam_android_v10.classes.Project;
import com.example.findteam_android_v10.classes.User;
import com.example.findteam_android_v10.fragments.FragMyProjects;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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
    DetailMyProjectAdapter detailMyProjectAdapter;
    ImageButton ibGoBack;
    Button btJoinProject, btLeaveProject;
    FloatingActionButton btChatProject;
    List<String> picturesURLs;
    List<Bitmap> pictureFiles;
    GalleryCreateProjectAdapter adapter;
    RecyclerView rvGallery;
    PopupWindow popupWindow;
   List<String> categories ;
    List<List<String>>  tags ;
    ProjectDetailTaglAdapter projectDetailTaglAdapter;
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

        this.ibEditProject= findViewById(R.id.ibEditProject);
        this.ibDeleteProject= findViewById(R.id.ibDeleteProject);
        this.ibGoBack = findViewById(R.id.goBackFab);

        categories = new ArrayList<>();
        tags = new ArrayList<>();

        //setup recyclerview and adapter for tags
        RecyclerView rvTags = findViewById(R.id.rvProjectDetailTags);
        projectDetailTaglAdapter = new ProjectDetailTaglAdapter(this, categories, tags);
        rvTags.setAdapter(projectDetailTaglAdapter);
        rvTags.setLayoutManager(new LinearLayoutManager(this));

        ibGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        this.btJoinProject = findViewById(R.id.btJoinProject);
        this.btLeaveProject = findViewById(R.id.btLeaveProject);
        this.btChatProject = findViewById(R.id.btChatProject);
        try {
            project = new JSONObject(getIntent().getStringExtra("project"));
            int memType = Project.getUserMembershipType(LoginActivity.currentUser.getInt("uid"), project);
            setControl(memType);

            if(LoginActivity.currentUser.getInt("uid") == project.getInt("owner_uid")) memType = Project.MEMBER_SHIP__TYPE_OWNER;
            Log.d(TAG, "Membership_type: " + memType);
            btChatProject.setOnClickListener(l -> {
                Intent i = new Intent(context, MainActivity.class);
                try {
                    i.putExtra("puid", project.getInt("pid"));
                    i.putExtra("is_user", false);
                    i.putExtra("title", project.getString("title"));
                    i.putExtra("request", MainActivity.REQUEST_CHAT_HISTORY);
                    startActivity(i);
                } catch (JSONException exception) {
                    exception.printStackTrace();
                }
            });


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

                members = new JSONArray();
                detailMyProjectAdapter = new DetailMyProjectAdapter(context, members, project);
                this.rvMembers.setAdapter(detailMyProjectAdapter);
                this.rvMembers.setLayoutManager(new LinearLayoutManager(context));

                FindTeamClient.get(User.GET_USER_URL + project.getString("owner_uid"), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                        try {
                            Log.d(TAG, "SHOW MEMBER:" + responseBody);
                            JSONObject owner = new JSONObject();
                            owner.put("uid", responseBody.getString("uid"));
                            owner.put("membership_type", Project.MEMBER_SHIP__TYPE_OWNER);
                            members = project.getJSONArray("members");
                            detailMyProjectAdapter.clear();
                            detailMyProjectAdapter.addHead(members, owner);
                        } catch (JSONException exception) {
                            exception.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject responseBody) {
                        Log.d(TAG, "FAILURE: " + statusCode + " -- " + responseBody);
                    }
                });



            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONArray tagsJson = project.getJSONArray("tags");
            //load the tags of the user
            for (int i = 0; i < tagsJson.length(); i++) {
                JSONObject tagObj = (JSONObject) tagsJson.get(i);
                if (!categories.contains(tagObj.getString("category"))) {
                    categories.add(tagObj.getString("category"));
                }

            }

            for (int i = 0; i < categories.size(); i++) {
                List<String> localTags = new ArrayList<>();
                for (int j = 0; j < tagsJson.length(); j++) {
                    JSONObject tagObj = (JSONObject) tagsJson.get(j);
                    if (categories.get(i).equals(tagObj.getString("category"))) {
                        localTags.add(tagObj.getString("text"));
                    }
                }
                tags.add(localTags);
            }

            projectDetailTaglAdapter.notifyDataSetChanged();

        } catch (JSONException exception) {
            exception.printStackTrace();
        }
    }

    public void setControl(int memType){
        Log.d(TAG, "SET CONTROL: MemType = " + memType);
        switch (memType){
            case Project.MEMBER_SHIP__TYPE_ADMIN:
            case Project.MEMBER_SHIP__TYPE_OWNER:{
                Log.d(TAG, "Owner/admin!!!");
                ibEditProject.setVisibility(View.VISIBLE);
                ibDeleteProject.setVisibility(View.VISIBLE);
                btJoinProject.setVisibility(View.INVISIBLE);
                btLeaveProject.setVisibility(View.INVISIBLE);
                ibDeleteProject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            FindTeamClient.get(Project.getURLGetProject(project.getInt("pid")), new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                                    try {
                                        int mem_type = Project.getUserMembershipType(LoginActivity.currentUser.getInt("uid"), responseBody);
                                        if(mem_type == Project.MEMBER_SHIP__TYPE_OWNER){
                                            onButtonDeletePopupWindowClick(v, project.getInt("pid"));
                                        }else{
                                            //TODO
                                            notOwnerPopup(v);
                                        }
                                    } catch (JSONException exception) {
                                        exception.printStackTrace();
                                    }


                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject responseBody) {

                                }
                            });
                        } catch (JSONException exception) {
                            exception.printStackTrace();
                        }

                        Log.d(TAG, "On Delete Project Button");
                    }
                });
                ibEditProject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            FindTeamClient.get(Project.getURLGetProject(project.getInt("pid")), new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                                    try {
                                        int mem_type = Project.getUserMembershipType(LoginActivity.currentUser.getInt("uid"), responseBody);
                                        if(mem_type == Project.MEMBER_SHIP__TYPE_OWNER ||
                                        mem_type == Project.MEMBER_SHIP__TYPE_ADMIN){
                                            Intent i = new Intent(context, UpdateProjectActivity.class);
                                            try {
                                                JSONArray iMembers = project.getJSONArray("members");
                                                for(int j = 0; j< iMembers.length(); j++){
                                                    if(iMembers.getJSONObject(j).getInt("uid") == project.getInt("owner_uid"));
                                                    iMembers.remove(j);
                                                    break;
                                                }
                                                Log.d(TAG, "iMembers: " + iMembers);
                                                i.putExtra("project", project.toString());
                                                startActivityForResult(i, EDIT_PROJECT_CODE);
                                                Log.d(TAG, "On Edit Project Button");
                                            } catch (JSONException exception) {
                                                exception.printStackTrace();
                                            }
                                        }else{
                                            //TODO
                                            notOwnerOrAdminPopup(v);
                                        }
                                    } catch (JSONException exception) {
                                        exception.printStackTrace();
                                    }


                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject responseBody) {

                                }
                            });
                        } catch (JSONException exception) {
                            exception.printStackTrace();
                        }


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
                Log.d(TAG, "SET CONTROL: ROLE = GUEST" );
                ibEditProject.setVisibility(View.INVISIBLE);
                ibDeleteProject.setVisibility(View.INVISIBLE);
                btJoinProject.setVisibility(View.VISIBLE);
                btLeaveProject.setVisibility(View.INVISIBLE);
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

                        JSONArray tagsJson = project.getJSONArray("tags");
                        //load the tags of the user
                        for (int i = 0; i < tagsJson.length(); i++) {
                            JSONObject tagObj = (JSONObject) tagsJson.get(i);
                            if (!categories.contains(tagObj.getString("category"))) {
                                categories.add(tagObj.getString("category"));
                            }

                        }

                        for (int i = 0; i < categories.size(); i++) {
                            List<String> localTags = new ArrayList<>();
                            for (int j = 0; j < tagsJson.length(); j++) {
                                JSONObject tagObj = (JSONObject) tagsJson.get(j);
                                if (categories.get(i).equals(tagObj.getString("category"))) {
                                    localTags.add(tagObj.getString("text"));
                                }
                            }
                            tags.add(localTags);
                        }

                        projectDetailTaglAdapter.notifyDataSetChanged();

                        adapter.clear();
                        adapter.addAll(Project.getPictures(resultProject));

                        members = resultProject.getJSONArray("members");
                        FindTeamClient.get(User.GET_USER_URL + resultProject.getString("owner_uid"), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                                try {
                                    Log.d(TAG, "SHOW MEMBER:" + responseBody);
                                    JSONObject owner = new JSONObject();
                                    owner.put("uid", responseBody.getString("uid"));
                                    owner.put("membership_type", Project.MEMBER_SHIP__TYPE_OWNER);
                                    Log.d(TAG, "OnActivityResult: ");
                                    detailMyProjectAdapter.clear();
                                    Log.d(TAG, "ACTIVITY_RESULT: members=" + members);
                                    detailMyProjectAdapter.addHead(members, owner);
                                } catch (JSONException exception) {
                                    exception.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject responseBody) {
                                Log.d(TAG, "FAILURE: " + statusCode + " -- " + responseBody);
                            }
                        });
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
            case Project.STATUS_IN_PROGRESS_INT:{
                tvProjectStatus.setText(Project.STATUS_IN_PROGRESS_STRING);
                ivStatus.setImageResource(R.drawable.ic_project_status_in_progress_green);
                break;
            }
            case Project.STATUS_IN_AWAITING_INT:{
                tvProjectStatus.setText(Project.STATUS_IN_AWAITING_STRING);
                ivStatus.setImageResource(R.drawable.ic_project_status_in_pending_green);
                break;
            }
            case Project.STATUS_IN_FINISHED_INT:{
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
                    deleteProject(pid);
                } catch (JSONException exception) {
                    exception.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
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

    public void notOwnerPopup(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_you_are_not_owner, null);
        TextView tvPopup_Invalid_Inputs = popupView.findViewById(R.id.tvMessage);
        tvPopup_Invalid_Inputs.setText("Not Owner!");
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

    }
    public void notOwnerOrAdminPopup(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_you_are_not_admin_or_owner, null);
        TextView tvPopup_Invalid_Inputs = popupView.findViewById(R.id.tvMessage);
        tvPopup_Invalid_Inputs.setText("Not an Admin or Owner!");

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

    }
}