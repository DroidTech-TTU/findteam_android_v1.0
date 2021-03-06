package com.example.findteam_android_v10.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.findteam_android_v10.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import co.lujun.androidtagview.TagContainerLayout;

public class ProfileTagAdapter extends RecyclerView.Adapter<ProfileTagAdapter.ViewHolder> {

    private final List<String> categories;
    private final List<List<String>> tags;

    private final String TAG = "ProfileTagAdapter";
    private final Context context;

    public ProfileTagAdapter(Context context, List<String> categories, List<List<String>> tags) {
        this.context = context;
        this.categories = categories;
        this.tags = tags;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_profile_tag, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String category = categories.get(position);
        List<String> tag = tags.get(position);
        holder.bind(category, tag);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView categoryText;
        public TagContainerLayout tagContainer;

        public ViewHolder(View view) {
            super(view);
            this.categoryText = view.findViewById(R.id.tagName);
            this.tagContainer = view.findViewById(R.id.tagContainer);

            Typeface typeface = ResourcesCompat.getFont(context, R.font.questrial);
            tagContainer.setTagTypeface(typeface);

        }

        public void bind(String category, List<String> tag){
            categoryText.setText(category);
            tagContainer.setTags(tag);
        }
    }


}
