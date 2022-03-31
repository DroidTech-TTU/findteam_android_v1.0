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

public class urlAdapter extends RecyclerView.Adapter<urlAdapter.ViewHolder> {
    private List<String> urls;
    private final String TAG = "urlAdapter";
    Context context;

    public urlAdapter(Context context,List<String> urls) {
        this.urls = urls;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_url, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String url = urls.get(position);
        holder.bind(url);
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_url;

        public ViewHolder(View view) {
            super(view);
            this.tv_url = view.findViewById(R.id.tv_url);
        }

        public void bind(String url){
            tv_url.setText(url);
        }
    }
}
