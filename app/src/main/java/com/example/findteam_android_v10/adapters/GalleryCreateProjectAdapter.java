package com.example.findteam_android_v10.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.findteam_android_v10.R;

import java.util.List;

public class GalleryCreateProjectAdapter extends RecyclerView.Adapter<GalleryCreateProjectAdapter.galleryViewHolder>{
    Context context;
    List<String> pictureURLs;
    public static String TAG = "galleryCreateProjectAdapter";

    public GalleryCreateProjectAdapter(Context context, List<String> picturesUrls) {
        this.pictureURLs = picturesUrls;
        this.context = context;
        //Log.d(TAG, String.valueOf(this.pictureURLs));
    }
    @NonNull
    @Override
    public GalleryCreateProjectAdapter.galleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View galleryView =  LayoutInflater.from(context).inflate(R.layout.item_galary_create_project, parent, false);
        return new galleryViewHolder(galleryView);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryCreateProjectAdapter.galleryViewHolder holder, int position) {
        Log.d(TAG, String.valueOf(this.pictureURLs.get(position)));
        holder.bind(pictureURLs.get(position));
    }

    @Override
    public int getItemCount() {
        return pictureURLs.size();
    }

    public class galleryViewHolder extends RecyclerView.ViewHolder {


        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView ivItemGallery;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public galleryViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            ivItemGallery = itemView.findViewById(R.id.ivItemGallery);
        }
        public void bind(String pictureURL) {
            Glide.with(context).load(pictureURL).into(ivItemGallery);
        }

    }
}
