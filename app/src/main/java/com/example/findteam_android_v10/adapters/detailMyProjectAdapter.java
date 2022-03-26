package com.example.findteam_android_v10.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findteam_android_v10.R;
import com.example.findteam_android_v10.classes.UserProject;

import java.util.List;

public class detailMyProjectAdapter extends RecyclerView.Adapter<detailMyProjectAdapter.ViewHolder> {
    private List<UserProject> members;
    private final String TAG = "detailMyProjectAdapter";

    public detailMyProjectAdapter(List<UserProject> members) {
        this.members = members;
        Log.d(TAG, String.valueOf(this.members));
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
        UserProject member = members.get(position);
        Log.d(TAG, String.valueOf(position));
        // Set item views based on your views and data model
        holder.bind(member);
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, String.valueOf(members.size()));
        return members.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView memberName;
        public TextView status;
        public TextView role;
        public ViewHolder(View view) {
            super(view);
            this.memberName = (TextView) view.findViewById(R.id.tvMemberNameDetailMyProject);
            this.role = (TextView) view.findViewById(R.id.tvRoleDetailMyProject);
            this.status = (TextView) view.findViewById(R.id.tvStatusDetailMyProject);
        }
        public void bind(UserProject member){
//            memberName.setText(member.getUserName());
            role.setText(member.getRole());
            status.setText(member.getStatus());
        }
    }
}
