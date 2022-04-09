package com.example.findteam_android_v10.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.findteam_android_v10.DetailMyProjectActivity;
import com.example.findteam_android_v10.FindTeamClient;
import com.example.findteam_android_v10.R;
import com.example.findteam_android_v10.classes.Project;
import com.example.findteam_android_v10.classes.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import cz.msebera.android.httpclient.Header;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private JSONArray chats;
    private Context context;

    public ChatListAdapter(Context context, JSONArray chats) {
        this.context = context;
        this.chats = chats;
    }

    @NonNull
    @Override
    public ChatListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_chat_list, parent, false);
        // Return a new holder instance
        return new ChatListAdapter.ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListAdapter.ViewHolder holder, int position) {
        try {
            holder.bind(chats.getInt(position));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return chats.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView chatListTextView;
        public ImageView chatListImageView;
        private View parent;

        public ViewHolder(View view) {
            super(view);
            parent = view;
            chatListImageView = view.findViewById(R.id.chatListImageView);
            chatListTextView = view.findViewById(R.id.chatListTextView);
        }

        public void bind(int uid) throws JSONException {
            FindTeamClient.get("user?uid=" + uid, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    parent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO launch intent to chat
                        }
                    });
                    try {
                        Log.d("ChatListAdapter", response.toString());
                        chatListTextView.setText(response.getString("first_name") + " ");
                        if (!response.getString("middle_name").isEmpty()) {

                            chatListTextView.append(response.getString("middle_name") + " ");

                        }
                        chatListTextView.append(response.getString("last_name"));
                        Object picture = response.get("picture");
                        if (picture == null) {

                            Glide.with(context)
                                    .load("https://findteam.2labz.com/picture/" + picture)
                                    .into(chatListImageView);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

}
