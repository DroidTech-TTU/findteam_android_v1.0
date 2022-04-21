package com.example.findteam_android_v10.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.findteam_android_v10.MemberProfileActivity;
import com.example.findteam_android_v10.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import co.lujun.androidtagview.TagContainerLayout;

public class ProfilesAdapter extends RecyclerView.Adapter<ProfilesAdapter.MyProjectViewHolder> {

    public static String TAG = "ProfilesAdapter";
    private JSONArray usersList;
    private final Context context; // individual view for the user of this page

    public ProfilesAdapter(Context context, JSONArray usersList) {
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public MyProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_profiles, parent, false);
        // Return a new holder instance
        return new MyProjectViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyProjectViewHolder holder, int position) {
        // Get the data model based on position
//        Project project = projects.get(position);
        //get user[position]
        try {
            JSONObject user = usersList.getJSONObject(position);
            //load picture
            Glide.with(context)
                    .load("https://findteam.2labz.com/picture/" + user.getString("picture")).into(holder.ivProfilePicture);

            //load name
            holder.twUserName.setText(user.getString("first_name") + " " + user.getString("last_name"));
            //load tags
            JSONArray tags = user.getJSONArray("tags");
            List <String> usertags = new ArrayList<>();
            for(int i=0; i<tags.length(); i++){
                JSONObject tag = tags.getJSONObject(i);
                usertags.add(tag.getString("text"));
            }
            holder.UserTags.setTags(usertags);

            holder.itProfiles.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, MemberProfileActivity.class);
                    try {
                        i.putExtra("fullname", user.getString("first_name") +
                                " " + user.getString("middle_name") + " " +
                                user.getString("last_name"));
                        i.putExtra("user", user.toString());

                        context.startActivity(i);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return usersList.length();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class MyProjectViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView twUserName;
        public ImageView ivProfilePicture;
        public TagContainerLayout UserTags;
        public ConstraintLayout itProfiles;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public MyProjectViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            twUserName = itemView.findViewById(R.id.twUserName);
            ivProfilePicture = itemView.findViewById(R.id.ProfilePicture);
            UserTags = itemView.findViewById(R.id.tgUserTags);
            itProfiles = itemView.findViewById(R.id.itProfiles);

            Typeface typeface = ResourcesCompat.getFont(context, R.font.questrial);
            UserTags.setTagTypeface(typeface);

        }

    }

    public void clear(){
        usersList = new JSONArray();
        notifyDataSetChanged();
    }
    public  void addAll(JSONArray newUsersList){
        usersList = newUsersList;
        notifyDataSetChanged();
    }
}
