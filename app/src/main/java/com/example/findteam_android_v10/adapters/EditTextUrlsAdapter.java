package com.example.findteam_android_v10.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findteam_android_v10.R;

import java.util.List;

public class EditTextUrlsAdapter extends RecyclerView.Adapter<EditTextUrlsAdapter.ViewHolder> {

    private final List<String> urls;
    private final String TAG = "EditTextUrlsAdapter";
    Context context;

    public EditTextUrlsAdapter(Context context, List<String> urls) {
        this.urls = urls;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_edit_text_url, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String url = urls.get(position);
        holder.bind(url, position);
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public EditText etUrl;
        public ImageView removeUrl;

        public ViewHolder(View view) {
            super(view);
            this.etUrl = view.findViewById(R.id.etUrl);
            this.removeUrl = view.findViewById(R.id.removeUrl);
        }

        public void bind(String url, int position){
            etUrl.setText(url);
            removeUrl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    urls.remove(position);
                    notifyDataSetChanged();
                }
            });
        }
    }


}
