package com.example.findteam_android_v10.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import co.lujun.androidtagview.TagContainerLayout;
import cz.msebera.android.httpclient.Header;


public class MyProjectsAdapter extends RecyclerView.Adapter<MyProjectsAdapter.MyProjectViewHolder> {
    private JSONArray jsonProjects;
    Context context;
    public static String TAG = "MyProjectsAdapter";

    public MyProjectsAdapter(Context context, JSONArray jsonProjects) {
        Log.d(TAG, jsonProjects.toString());
        this.context = context;
        this.jsonProjects = jsonProjects;
    }

    @NonNull
    @Override
    public MyProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_my_project, parent, false);
        // Return a new holder instance
        return new MyProjectViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyProjectViewHolder holder, int position) {
        // Get the data model based on position
        try {
            JSONObject project = (JSONObject) jsonProjects.get(position);
            Log.d(TAG, "itMyProject.setOnLongClickListener: positon =" + position + "--" + project.toString());
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
                        i.putExtra("project", project.toString());
                        context.startActivity(i);
                    } catch (JSONException e) {
                        Log.d(TAG, "itMyProject.setOnLongClickListener: " + e.toString());
                    }

                }
            });

//            "user?uid="
            FindTeamClient.get(User.GET_USER_URL + project.getString("owner_uid"), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        String ownerNameText = response.getString("first_name") + " " + response.getString("last_name");
                        holder.tvOwner.setText(ownerNameText);

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

            int memType = Project.getUserMembershipType(LoginActivity.currentUser.getInt("uid"), project);
            String role = Project.getMemTypeString(memType);
            holder.tvRole.setText(role);

            List<String> tags = Project.getTagsList(project);

            Typeface typeface = ResourcesCompat.getFont(context, R.font.questrial);
            holder.myProjectsTags.setTagTypeface(typeface);
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
    public static class MyProjectViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView twProjectName;
        public TextView twProjectStatus;
        public TextView tvOwner;
        public TextView tvRole;
        ConstraintLayout itMyProject;

        TagContainerLayout myProjectsTags;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public MyProjectViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            twProjectName = itemView.findViewById(R.id.twProjectName);
            twProjectStatus = itemView.findViewById(R.id.twProjectStatus);
            myProjectsTags = itemView.findViewById(R.id.tgMyProjects);
            tvOwner = itemView.findViewById(R.id.tvOwnerMyProjects);
            tvRole = itemView.findViewById(R.id.tvRoleMyProjects);
            itMyProject = itemView.findViewById(R.id.itMyProject);


        }

    }

    public void clear() {
        this.jsonProjects = new JSONArray();
        notifyDataSetChanged();
    }

    public void addAll(JSONArray jsonProjects) {
        this.jsonProjects = jsonProjects;
        notifyDataSetChanged();
    }
}
