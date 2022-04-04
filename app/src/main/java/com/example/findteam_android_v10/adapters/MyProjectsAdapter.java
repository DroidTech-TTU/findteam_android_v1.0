package com.example.findteam_android_v10.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findteam_android_v10.DetailMyProjectActivity;
import com.example.findteam_android_v10.FindTeamClient;
import com.example.findteam_android_v10.LoginActivity;
import com.example.findteam_android_v10.R;
import com.example.findteam_android_v10.classes.Project;
import com.example.findteam_android_v10.classes.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;
import cz.msebera.android.httpclient.Header;


public class MyProjectsAdapter extends RecyclerView.Adapter<MyProjectsAdapter.myProjectViewHolder>{
    private JSONArray jsonProjects;
    Context context;
    public static String TAG = "myProjectsAdapter";

    public MyProjectsAdapter(Context context, JSONArray jsonProjects) {
        Log.d(TAG, jsonProjects.toString());
        this.context = context;
        this.jsonProjects = jsonProjects;

    }

    @NonNull
    @Override
    public myProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_my_project, parent, false);
        // Return a new holder instance
        myProjectViewHolder viewHolder = new myProjectViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull myProjectViewHolder holder, int position) {
        // Get the data model based on position
//        Project project = projects.get(position);
        try {
            JSONObject project = (JSONObject) jsonProjects.get(position);
            Log.d(TAG, "itMyProject.setOnLongClickListener: positon =" + position + "--" +project.toString());
            // Set item views based on your views and data model
            holder.twProjectName.setText(project.getString("title"));
            holder.twProjectStatus.setText(Project.getStringStatus(project.getInt("status")));
            holder.itMyProject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "itMyProject.setOnLongClickListener: project = " + project.toString());

                    try {
                        Intent i = new Intent(context, DetailMyProjectActivity.class);
                        Log.d(TAG, "itMyProject.setOnLongClickListener: " + project.getInt("pid"));
                        i.putExtra("pid", project.getInt("pid"));
                        context.startActivity(i);
                    } catch (JSONException e) {
                        Log.d(TAG, "itMyProject.setOnLongClickListener: " + e.toString());
                    }

                }
            });

//            "user?uid="
            FindTeamClient.get(User.GET_USER_URL + project.getString("owner_uid"), new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        JSONObject user = response;
                        holder.tvOwner.setText(user.getString("first_name") + " " + user.getString("last_name"));
                        Log.d(TAG, "onBindViewHolder: Success");

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG, "onBindViewHolder: On Failure");
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e(TAG, statusCode + " " + responseString + " " + throwable);
                }
            });

            JSONArray members = project.getJSONArray("members");
            for(int i = 0; i <members.length(); i++){
                JSONObject member = members.getJSONObject(i);
                if(LoginActivity.currentUser.getInt("uid") == member.getInt("uid")){
                    String role = Project.getMemType(member.getInt("membership_type"));
                    holder.tvRole.setText(role);
                }
            }
            holder.itMyProject.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    Intent i = new Intent(context, DetailMyProjectActivity.class);
                    try {
                        i.putExtra("pid", project.getInt("pid"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    context.startActivity(i);
                    return false;
                }
            });

            List<String> tags = Project.getTagsList(project);
            holder.myProjectsTags.setTags(tags);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return jsonProjects.length();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class myProjectViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView twProjectName;
        public TextView twProjectStatus;
        public TagContainerLayout my_projects_tags;
        public TextView tvOwner;
        public TextView tvRole;
        ConstraintLayout itMyProject;

        TagContainerLayout myProjectsTags;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public myProjectViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            twProjectName =  itemView.findViewById(R.id.twProjectName);
            twProjectStatus = itemView.findViewById(R.id.twProjectStatus) ;
            myProjectsTags = itemView.findViewById(R.id.tgMyProjects);
            tvOwner = itemView.findViewById(R.id.tvOwnerMyProjects);
            tvRole = itemView.findViewById(R.id.tvRoleMyProjects);
            itMyProject = itemView.findViewById(R.id.itMyProject);


        }

    }

    public void clear(){
        this.jsonProjects = new JSONArray();
        notifyDataSetChanged();
    }
    public void addAll(JSONArray jsonProjects) {
        this.jsonProjects = jsonProjects;
        notifyDataSetChanged();
    }

    public void getUser(){

    }
}