package com.example.findteam_android_v10.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findteam_android_v10.CreateProjectActivity;
import com.example.findteam_android_v10.FindTeamClient;
import com.example.findteam_android_v10.R;
import com.example.findteam_android_v10.adapters.myProjectsAdapter;
import com.example.findteam_android_v10.classes.Project;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;

public class FragMyProjects extends Fragment{
    public static String TAG = "FragMyProjects";
    RecyclerView rvContacts;
    String[] projectNames = {"Project 1", "Project 2", "Project 3", "Project 4", "Project 1", "Project 2", "Project 3", "Project 4"
    ,"Project 1", "Project 2", "Project 3", "Project 4","Project 1", "Project 2", "Project 3", "Project 4","Project 1", "Project 2", "Project 3", "Project 4","Project 1", "Project 2", "Project 3", "Project 4", "Project 1", "Project 2", "Project 3", "Project 4"
            ,"Project 1", "Project 2", "Project 3", "Project 4","Project 1", "Project 2", "Project 3", "Project 4","Project 1", "Project 2", "Project 3", "Project 4"};
    ArrayAdapter<String> arrayAdapter;

    View btCreateProject;

    ArrayList<Project> projects;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "On create");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "On results");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_my_projects, container, false);
        this.rvContacts = (RecyclerView) view.findViewById(R.id.rvMyProjectsSearch);
        this.btCreateProject = view.findViewById(R.id.btCreateMyProject);

        // String URL = "";
        // FindTeamClient.get();

        btCreateProject.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CreateProjectActivity.class);
                view.getContext().startActivity(intent);
            }
        });

        // Initialize contacts
        projects = Project.creatProjectsList(40);
        // Create adapter passing in the sample user data
        myProjectsAdapter adapter = new myProjectsAdapter(projects);
        // Attach the adapter to the recyclerview to populate items
        rvContacts.setAdapter(adapter);
        // Set layout manager to position the items
        rvContacts.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.my_projects_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.btSearchProjects);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type here to search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                arrayAdapter.getFilter().filter(s);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
}
