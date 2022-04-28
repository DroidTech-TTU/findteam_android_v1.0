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
    private RecyclerView rvMembers;
    private Context context;
    private TextView tvDescription;
    private TextView tvProjectTitleDetailProject;
    private TextView tvProjectStatus;
    private ImageButton ibEditProject;
    private ImageButton ibDeleteProject;
    private ImageView ivStatus;
    private JSONObject project;
    private JSONArray members;
    private DetailMyProjectAdapter detailMyProjectAdapter;
    private ImageButton ibGoBack;
    private Button btJoinProject, btLeaveProject;
    private FloatingActionButton btChatProject;
    private List<String> picturesURLs;
    private List<Bitmap> pictureFiles;
    private GalleryCreateProjectAdapter adapter;
    private RecyclerView rvGallery;
    private PopupWindow popupWindow;
    private List<String> categories ;
    private List<List<String>>  tags ;
    private ProjectDetailTaglAdapter projectDetailTaglAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_my_project);//load view for detail project
        context = this;//get context

        //activity detail my project items
        this.pictureFiles = new ArrayList<>();
        this.rvGallery = findViewById(R.id.rvGalleryDetailProject);
        this.rvMembers = (RecyclerView) findViewById(R.id.rvMembers);
        this.tvDescription = (TextView) findViewById(R.id.tvDescriptionMyDetailProjects);
        this.tvProjectStatus = (TextView) findViewById(R.id.tvStatusMyProjectDetailMain);
        this.ivStatus = (ImageView) findViewById(R.id.ivStatusMyProjectDetail) ;
        this.tvProjectTitleDetailProject = findViewById(R.id.tvProjecTitleDetailProject);
        this.ibEditProject= findViewById(R.id.ibEditProject);
        this.ibDeleteProject= findViewById(R.id.ibDeleteProject);
        this.ibGoBack = findViewById(R.id.goBackFab);
        this.btJoinProject = findViewById(R.id.btJoinProject);
        this.btLeaveProject = findViewById(R.id.btLeaveProject);
        this.btChatProject = findViewById(R.id.btChatProject);

        //Project tags
        this.categories = new ArrayList<>();
        this.tags = new ArrayList<>();

        //setup recyclerview and adapter for tags
        RecyclerView rvTags = findViewById(R.id.rvProjectDetailTags);
        this.projectDetailTaglAdapter = new ProjectDetailTaglAdapter(this, this.categories, this.tags);
        rvTags.setAdapter(this.projectDetailTaglAdapter);
        rvTags.setLayoutManager(new LinearLayoutManager(this));

        //Return to parent activity
        this.ibGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        try {
            //Configure showing features base on roles: owner, admin, member, guest
            this.project = new JSONObject(getIntent().getStringExtra("project")); //receive project from parent activity
            int memType = Project.getUserMembershipType(LoginActivity.currentUser.getInt("uid"), project); //get user type in this project
            setControl(memType); //set controlling items base on roles

            //Chat button on click navigate to chat history
            //Path: main activity/chat history fragment
            btChatProject.setOnClickListener(l -> {
                Intent i = new Intent(context, MainActivity.class); //Intent to Main activity
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

            //Show project's details
            try {
                //Show project's pictures
                picturesURLs = new ArrayList<>();
                picturesURLs = Project.getPictures(project); //Get Picture URLS

                //call GalleryCreateProjectAdapter construction
                adapter = new GalleryCreateProjectAdapter(context, picturesURLs);
                // Attach the adapter to the recyclerview to populate items
                rvGallery.setAdapter(adapter);
                // Set layout manager to position the items
                rvGallery.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)); //Display horizontal

                //Show title, description, status
                Log.d(TAG, "On Create: " + project.toString());
                tvProjectTitleDetailProject.setText(project.getString("title"));
                tvDescription.setText(project.getString("description"));
                updateImageStatus(project.getInt("status"));

                //Show members list
                members = new JSONArray();
                detailMyProjectAdapter = new DetailMyProjectAdapter(context, members, project); //call DetailMyProjectAdapter construction
                this.rvMembers.setAdapter(detailMyProjectAdapter);
                this.rvMembers.setLayoutManager(new LinearLayoutManager(context));//display vertical

                //add owner user on top of DetailMyProjectAdapter and rvMembers
                //GET request owner information
                FindTeamClient.get(User.GET_USER_URL + project.getString("owner_uid"), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                        try {
                            //create new JSONObject for owner user
                            Log.d(TAG, "SHOW MEMBER:" + responseBody);
                            JSONObject owner = new JSONObject();
                            owner.put("uid", responseBody.getString("uid"));
                            owner.put("membership_type", Project.MEMBER_SHIP__TYPE_OWNER);

                            //Add to existing members list
                            members = project.getJSONArray("members");

                            //clear and show the DetailMyProjectAdapter and rvMembers again
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

            //load categories and add tags into the categories
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

            //Show tags
            projectDetailTaglAdapter.notifyDataSetChanged();

        } catch (JSONException exception) {
            exception.printStackTrace();
        }
    }

    //Set what will be displayed base on roles
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

                //Delete Project Button on click
                ibDeleteProject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Double check roles before make an API request
                        try {
                            FindTeamClient.get(Project.getURLGetProject(project.getInt("pid")), new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                                    try {
                                        //Get membership type
                                        int mem_type = Project.getUserMembershipType(LoginActivity.currentUser.getInt("uid"), responseBody);
                                       //if owner then call the delete request
                                        if(mem_type == Project.MEMBER_SHIP__TYPE_OWNER){
                                            onButtonDeletePopupWindowClick(v, project.getInt("pid"));
                                        }else{
                                            //Show popup not owner
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

                //Edit Project Button on click
                ibEditProject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            //Double check user role before make an API request
                            FindTeamClient.get(Project.getURLGetProject(project.getInt("pid")), new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                                    try {
                                        //Get membership type
                                        int mem_type = Project.getUserMembershipType(LoginActivity.currentUser.getInt("uid"), responseBody);
                                        //if owner or admin then intent to edit activity
                                        if(mem_type == Project.MEMBER_SHIP__TYPE_OWNER ||
                                             mem_type == Project.MEMBER_SHIP__TYPE_ADMIN){
                                            Intent i = new Intent(context, UpdateProjectActivity.class);
                                            i.putExtra("project", responseBody.toString());
                                            startActivityForResult(i, EDIT_PROJECT_CODE);
                                            Log.d(TAG, "On Edit Project Button");
                                        }else{
                                            ////Show popup not owner nor admin
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
                //Hide edit, delete, join, leave button
                ibEditProject.setVisibility(View.INVISIBLE);
                ibDeleteProject.setVisibility(View.INVISIBLE);
                btJoinProject.setVisibility(View.INVISIBLE);
                btLeaveProject.setVisibility(View.INVISIBLE);
                break;
            }
            case Project.MEMBER_SHIP__TYPE_GUEST:{
                Log.d(TAG, "SET CONTROL: ROLE = GUEST" );
                //Hide edit, delete, leave button
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

    //On a return from children activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //If return from Edit Project Activity
        if(data!=null && resultCode == EDIT_PROJECT_CODE){

            //Call a get Project API
            FindTeamClient.get(Project.getURLGetProject(data.getIntExtra("pid", -1)), new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {

                        //Set values for the GUI
                        JSONObject resultProject = new JSONObject(new String(responseBody));
                        Log.d(TAG, "On Activity Result: result = " + resultProject);
                        tvProjectTitleDetailProject.setText(resultProject.getString("title"));
                        tvDescription.setText(resultProject.getString("description"));
                        updateImageStatus(resultProject.getInt("status")); //Update project status icon


                        //load the tags of the user
                        JSONArray tagsJson = project.getJSONArray("tags");
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
                        //Refresh the tags adapter and recycle view
                        projectDetailTaglAdapter.notifyDataSetChanged();

                        //ReFresh the Project Pictures Adapter and recycle view
                        adapter.clear();
                        adapter.addAll(Project.getPictures(resultProject));

                        //clear and refresh the members adapter and recycle view
                        members = resultProject.getJSONArray("members");
                        FindTeamClient.get(User.GET_USER_URL + resultProject.getString("owner_uid"), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                                try {
                                    Log.d(TAG, "SHOW MEMBER:" + responseBody);
                                    //Create new owner
                                    JSONObject owner = new JSONObject();
                                    owner.put("uid", responseBody.getString("uid"));
                                    owner.put("membership_type", Project.MEMBER_SHIP__TYPE_OWNER);
                                    Log.d(TAG, "OnActivityResult: ");

                                    //Load to adapter
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

                //Intent to parent activity (Fragment my project)
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
        //Create new member
        JSONObject member = new JSONObject();
        member.put("uid", LoginActivity.currentUser.get("uid"));
        member.put("membership_type", Project.MEMBER_SHIP__TYPE_PENDING);
        JSONArray members = project.getJSONArray("members");
        members.put(member);

        Log.d(TAG, project.toString());
        //Call Join Project API
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