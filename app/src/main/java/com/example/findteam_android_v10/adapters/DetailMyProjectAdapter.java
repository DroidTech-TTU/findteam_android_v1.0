package com.example.findteam_android_v10.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findteam_android_v10.R;
import com.example.findteam_android_v10.classes.UserProject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailMyProjectAdapter extends RecyclerView.Adapter<DetailMyProjectAdapter.ViewHolder> {

    private final String TAG = "detailMyProjectAdapter";
    private JSONArray members;
    private  Context context;
    public DetailMyProjectAdapter(Context context, JSONArray members) {
        this.context = context;
        this.members = members;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
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
        return 0;
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
            JSONObject member = getMember();
            memberName.setText(member.getString("first_name") + " " + member.getString("last_name"));
            membership_type.setText(UserProject.getMemType(memberProject.getInt("membership_type")));

        }

        private JSONObject getMember() {
            return new JSONObject();
        }

    }

}
