package com.example.findteam_android_v10.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findteam_android_v10.FindTeamClient;
import com.example.findteam_android_v10.LoginActivity;
import com.example.findteam_android_v10.MainActivity;
import com.example.findteam_android_v10.MemberProfileActivity;
import com.example.findteam_android_v10.R;
import com.example.findteam_android_v10.classes.Project;
import com.example.findteam_android_v10.classes.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class DetailMyProjectAdapter extends RecyclerView.Adapter<DetailMyProjectAdapter.ViewHolder> {

    private final String TAG = "detailMyProjectAdapter";
    private JSONArray members;
    private  Context context;
    public DetailMyProjectAdapter(Context context, JSONArray members) {
        Log.d(TAG, "Constructor: " + members.toString());
        this.context = context;
        this.members = members;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        Log.d(TAG, "onCreateViewHolder: " + members.toString());
        View view = inflater.inflate(R.layout.item_my_project_detail, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            holder.bind(members.getJSONObject(position));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return members.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView memberName;
        public TextView membership_type;

        public ViewHolder(View view) {
            super(view);
            this.memberName = (TextView) view.findViewById(R.id.tvMemberNameDetailMyProject);
            this.membership_type = (TextView) view.findViewById(R.id.tvRoleDetailMyProject);
        }
        public void bind(JSONObject memberProject) throws JSONException {
            Log.d(TAG, "bind: " + memberProject.toString());
            FindTeamClient.get(User.GET_USER_URL+memberProject.getString("uid"), new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        memberName.setText(response.getString("first_name") + " " + response.getString("last_name"));
                        membership_type.setText(Project.getMemTypeString(memberProject.getInt("membership_type")));
                        Log.d(TAG, "BIND MEMBER: " + memberProject.getInt("membership_type"));
                        switch (memberProject.getInt("membership_type")){
                            case Project.MEMBER_SHIP__TYPE_OWNER: {
                                membership_type.setTextColor(Color.parseColor("#000000"));
                                //memberName.setTextColor(Color.parseColor("#000000"));
                                break;
                            }
                            case Project.MEMBER_SHIP__TYPE_MEMBER: {
                                membership_type.setTextColor(Color.parseColor("#007a16"));
                                //memberName.setTextColor(Color.parseColor("#5AFF00"));
                                break;
                            }
                            case Project.MEMBER_SHIP__TYPE_PENDING: {
                                membership_type.setTextColor(Color.parseColor("#7a0000"));
                                //memberName.setTextColor(Color.parseColor("#4a4948"));
                                break;
                            }
                            default:{
                                membership_type.setTextColor(Color.parseColor("#000000"));
                                memberName.setTextColor(Color.parseColor("#000000"));
                            }
                        }

/*                        if (LoginActivity.currentUser.getInt("uid") == memberProject.getInt("uid")) {
                            membership_type.setTextColor(Color.parseColor("#FF0000"));
                            memberName.setTextColor(Color.parseColor("#FF0000"));
                        }*/
                        Log.d(TAG, "onBindViewHolder: Success");

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG, "onBindViewHolder: On Failure-- " + statusCode );
                    }

                    //go to profile
                    memberName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(context, MemberProfileActivity.class);
                            try {
                                i.putExtra("fullname", response.getString("first_name") +
                                        " " + response.getString("middle_name") + " " +
                                        response.getString("last_name"));
                                i.putExtra("user", response.toString());

                                context.startActivity(i);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e(TAG, statusCode + " " + responseString + " " + throwable);
                }
            });



        }
        private JSONObject getMember() {
            return new JSONObject();
        }

    }

    public void clear(){
        members = new JSONArray();
        notifyDataSetChanged();
    }
    public void addTail(JSONArray members){
        this.members = members;
        notifyItemInserted(members.length());
    }
    public void addAll(JSONArray members){
       this.members = members;
        notifyDataSetChanged();
    }
}
