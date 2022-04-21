package com.example.findteam_android_v10.adapters;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.bumptech.glide.Glide;
import com.example.findteam_android_v10.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GalleryCreateProjectAdapter extends RecyclerView.Adapter<GalleryCreateProjectAdapter.galleryViewHolder> {
    private final Context context;
    private List<String> pictureURLs;
    public static String TAG = "GalleryCreateProjectAdapter";

    public GalleryCreateProjectAdapter(Context context, List<String> picturesUrls) {
        this.pictureURLs = picturesUrls;
        this.context = context;
    }

    @NonNull
    @Override
    public GalleryCreateProjectAdapter.galleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View galleryView = LayoutInflater.from(context).inflate(R.layout.item_galary_create_project, parent, false);
        return new galleryViewHolder(galleryView);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryCreateProjectAdapter.galleryViewHolder holder, int position) {
        Log.d(TAG, String.valueOf(this.pictureURLs.get(position)));
        Log.d(TAG, "position=" + position);
        String pictureURL = this.pictureURLs.get(position);
        String pos = String.valueOf(position);
        holder.ivItemGallery.setOnClickListener(v -> {
//                holder.onItemImagePopupWindowClick(v, Integer.parseInt(pos));
            holder.onItemImagePopupWindowClick(v, pictureURL);
//                holder.onItemImagePopupWindowClick(v, pictureURL, pos);
        });
        holder.bind(pictureURL);
    }

    @Override
    public int getItemCount() {
        return pictureURLs.size();
    }

    public void clear() {
        this.pictureURLs = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addAll(List<String> picturesURLs) {
        Log.d(TAG, "Add All" + picturesURLs);

        this.pictureURLs.addAll(picturesURLs);
        notifyDataSetChanged();
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
            Glide.with(context).load(pictureURL).centerCrop().into(ivItemGallery);
        }

        public void onItemImagePopupWindowClick(View view, String pictureURL) {
            Log.d(TAG, "OnItemImagePopup: " + pictureURL);
            // inflate the layout of the popup window
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.popup_view_photo, null);
            ImageView ivPopupImage = popupView.findViewById(R.id.ivPopupImage);
            Glide.with(context).load(pictureURL).into(ivPopupImage);

            // create the popup window
            int width = LinearLayout.LayoutParams.MATCH_PARENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
            // lets taps outside the popup also dismiss it
            final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
            // show the popup window
            // which view you pass in doesn't matter, it is only used for the window token
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

            // dismiss the popup window when touched


            popupView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popupWindow.dismiss();
                    return false;
                }
            });
        }
    }
}
