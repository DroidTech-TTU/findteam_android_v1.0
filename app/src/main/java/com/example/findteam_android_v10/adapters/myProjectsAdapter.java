package com.example.findteam_android_v10.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findteam_android_v10.DetailMyProjectActivity;
import com.example.findteam_android_v10.R;
import com.example.findteam_android_v10.classes.Project;

import java.util.List;

public class myProjectsAdapter extends RecyclerView.Adapter<myProjectsAdapter.ViewHolder> implements View.OnClickListener {
    private List<Project> projects;
    TextView twProjectName;
    TextView twProjectStatus;
    public myProjectsAdapter(List<Project> Projects) {
        super();
        projects = Projects;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);


        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_my_project, parent, false);
        twProjectName = (TextView) contactView.findViewById(R.id.twProjectName);
        twProjectStatus = (TextView) contactView.findViewById(R.id.twProjectStatus);
        twProjectName.setOnClickListener(this);
        twProjectStatus.setOnClickListener(this);
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data model based on position
        Project project = projects.get(position);

        // Set item views based on your views and data model
        TextView twName = holder.twProjectName;
        twName.setText(project.getName());
        TextView twStatus = holder.twProjectStatus;
        twStatus.setText(project.getName());
        Button button = holder.btView;
//        button.setText(String.valueOf(project.getId()));
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    @Override
    public void onClick(View view) {
        Intent i = new Intent(view.getContext(), DetailMyProjectActivity.class);
        view.getContext().startActivity(i);

    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView twProjectName;
        public TextView twProjectStatus;
        public Button btView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            twProjectName = (TextView) itemView.findViewById(R.id.twProjectName);
            twProjectStatus = (TextView) itemView.findViewById(R.id.twProjectStatus) ;
//            btView = (Button) itemView.findViewById(R.id.btView);
        }
    }

}
