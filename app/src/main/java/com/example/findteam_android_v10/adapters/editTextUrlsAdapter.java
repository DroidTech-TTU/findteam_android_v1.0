package com.example.findteam_android_v10.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findteam_android_v10.R;

import java.util.List;

public class editTextUrlsAdapter extends RecyclerView.Adapter<editTextUrlsAdapter.ViewHolder> {

    private List<String> urls;
    private final String TAG = "editTextUrlsAdapter";
    Context context;

    public editTextUrlsAdapter(Context context,List<String> urls) {
        this.urls = urls;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_edit_text_url, parent, false);
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
        public EditText etUrl;

        public ViewHolder(View view) {
            super(view);
            this.etUrl = view.findViewById(R.id.etUrl);
        }

        public void bind(String url){
            etUrl.setText(url);
        }
    }


}
