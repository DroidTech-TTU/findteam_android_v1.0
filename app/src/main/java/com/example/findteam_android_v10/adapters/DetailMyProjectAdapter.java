package com.example.findteam_android_v10.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findteam_android_v10.DetailMyProjectActivity;
import com.example.findteam_android_v10.FindTeamClient;
import com.example.findteam_android_v10.LoginActivity;
import com.example.findteam_android_v10.MainActivity;
import com.example.findteam_android_v10.MemberProfileActivity;
import com.example.findteam_android_v10.R;
import com.example.findteam_android_v10.classes.Project;
import com.example.findteam_android_v10.classes.User;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class DetailMyProjectAdapter extends RecyclerView.Adapter<DetailMyProjectAdapter.ViewHolder> {

    private final String TAG = "detailMyProjectAdapter";
    private JSONArray members;
    private  Context context;
    private JSONObject project;
    public DetailMyProjectAdapter(Context context, JSONArray members, JSONObject project) throws JSONException {
        Log.d(TAG, "Constructor: " + members.toString());
        this.context = context;
        this.members = members;
        this.project = project;
    }
    private void setProject(JSONObject project){
        this.project = project;
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
                        int currMemType = Project.getUserMembershipType(LoginActivity.currentUser.getInt("uid"), project);
                        Log.d(TAG, "memType: " + currMemType);
                        if( currMemType == Project.MEMBER_SHIP__TYPE_OWNER && memberProject.getInt("membership_type") != Project.MEMBER_SHIP__TYPE_OWNER){
                            membership_type.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        onButtonShowPopupWindowClick(v, response);
                                    } catch (JSONException exception) {
                                        exception.printStackTrace();
                                    }
                                }
                            });
                        }
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
        public void onButtonShowPopupWindowClick(View view, JSONObject member) throws JSONException {
            // inflate the layout of the popup window
            LayoutInflater inflater = (LayoutInflater)
                    view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.popup_review_member, null);

            // create the popup window
            int width = LinearLayout.LayoutParams.WRAP_CONTENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
            boolean focusable = true; // lets taps outside the popup also dismiss it
            final PopupWindow popupWindow = new PopupWindow(popupView,width,height,focusable);

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
            Button btAccept = popupView.findViewById(R.id.btAccept);
            Button btReject = popupView.findViewById(R.id.btReject);
            TextView tvName = popupView.findViewById(R.id.tvUserName);
            tvName.setText(member.getString("first_name") + " " + member.getString("last_name"));
            btAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        members.getJSONObject(getPosition()).put("membership_type", Project.MEMBER_SHIP__TYPE_MEMBER);
                        acceptMember();
                        notifyItemChanged(getPosition());
                        popupWindow.dismiss();
                    } catch (JSONException | UnsupportedEncodingException exception) {
                        exception.printStackTrace();
                    }

                }
            });
            btReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        members.remove(getPosition());
                        rejectMember();
                        notifyItemRemoved(getAdapterPosition());
                        popupWindow.dismiss();
                    } catch (JSONException | UnsupportedEncodingException exception) {
                        exception.printStackTrace();
                    }


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
    public void addHead(JSONArray nMembers, JSONObject member) throws JSONException {
        for(int i = members.length()-1; i>=0; i--){
            members.put(members.getJSONObject(i));
        }
        members.put(0, member);
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

    public JSONArray getMembers(){
        return members;
    }

    private void acceptMember() throws JSONException, UnsupportedEncodingException {
        setMemberRole(Project.MEMBER_SHIP__TYPE_MEMBER);
    }
    private void rejectMember() throws JSONException, UnsupportedEncodingException {
        setMemberRole(Project.MEMBER_SHIP__TYPE_REJECT);
    }
    private void setMemberRole(int role) throws JSONException, UnsupportedEncodingException {

        project.remove("members");
        project.put("members", members);

        Log.d(TAG, project.toString());
        String URL = Project.getURLUpdateProject(project.getInt("pid"));

        int tmpPid = project.getInt("pid");
        JSONArray tmpPics = project.getJSONArray("pictures");
        int tmpOwnerId = project.getInt("owner_uid");
        project.remove("pid");
        project.remove("pictures");
        project.remove("owner_uid");
        StringEntity entity = new StringEntity(project.toString());
        Log.d(TAG, "On set roles: " + project);
        project.put("pid", tmpPid);
        project.put("pictures", tmpPics);
        project.put("owner_uid", tmpOwnerId);
        Log.d(TAG, "Done set roles: " + project);
        FindTeamClient.post(context,URL, entity, new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i(TAG, "setRoleMember(): the status code for this request is: " + statusCode);
                try {
                    Log.i(TAG, "setRoleMember(): input : " + entity.getContent().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                try {
                    Log.i(TAG, "setRoleMember(): input : " + entity.getContent().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e(TAG, "saveProject(): : " + new String(responseBody));
                Log.e(TAG, "saveProject(): the status code for this request is" + statusCode);
                Toast.makeText(context, "Failure to create project", Toast.LENGTH_LONG).show();
            }

        });

    }
}
