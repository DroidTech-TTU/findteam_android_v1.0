package com.example.findteam_android_v10;

//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.example.findteam_android_v10.adapters.galleryCreateProjectAdapter;
import com.example.findteam_android_v10.adapters.myProjectsAdapter;
import com.example.findteam_android_v10.classes.Project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;

public class CreateProjectActivity extends AppCompatActivity {

    RecyclerView rvGallery;

    ArrayAdapter<String> arrayAdapter;
    Project project;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);

        List<String> skills = Arrays.asList("C#", "Python", "Java", "Software Developer","C#", "Python");
        TagContainerLayout myProjectsTags = findViewById(R.id.tgCreateProject);
        myProjectsTags.setTags(skills);

        this.rvGallery = findViewById(R.id.rvGallery);
//        // Initialize contacts
//        projects = Project.creatProjectsList(40);
//        // Create adapter passing in the sample user data
        List<String> picturesURLs = new ArrayList<String>();
        picturesURLs.addAll( Arrays.asList("https://www.gstatic.com/webp/gallery3/1.png", "https://www.gstatic.com/webp/gallery3/2.png", "https://www.gstatic.com/webp/gallery3/2.png",
                "https://www.gstatic.com/webp/gallery3/2.png", "https://www.gstatic.com/webp/gallery3/2.png",
                "https://www.gstatic.com/webp/gallery3/2.png", "https://www.gstatic.com/webp/gallery3/2.png"));
        galleryCreateProjectAdapter adapter = new galleryCreateProjectAdapter(this, picturesURLs);
//        // Attach the adapter to the recyclerview to populate items
        rvGallery.setAdapter(adapter);
//        // Set layout manager to position the items
        rvGallery.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
    }


}