package com.example.findteam_android_v10.adapters;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.findteam_android_v10.FindTeamClient;
import com.example.findteam_android_v10.LoginActivity;
import com.example.findteam_android_v10.R;
import com.example.findteam_android_v10.classes.User;
import com.example.findteam_android_v10.fragments.FragChatListDirections;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import cz.msebera.android.httpclient.Header;

public class ChatHistoryAdapter extends RecyclerView.Adapter<ChatHistoryAdapter.ViewHolder> {

    private JSONArray messages;
    private Context context;

    public ChatHistoryAdapter(Context context, JSONArray messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public ChatHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_chat_history, parent, false);
        // Return a new holder instance
        return new ChatHistoryAdapter.ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHistoryAdapter.ViewHolder holder, int position) {
        try {
            holder.bind(messages.getJSONObject(position));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return messages.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView messageTextView;
        public RelativeLayout messageLayout;

        public ViewHolder(View view) {
            super(view);
            messageTextView = view.findViewById(R.id.messageTextView);
            messageLayout = view.findViewById(R.id.messageLayout);
        }

        public void bind(JSONObject message) throws JSONException {
            if (message.getInt("from_uid") == LoginActivity.currentUser.getInt("uid")) {

                messageLayout.setBackground(AppCompatResources.getDrawable(context, R.drawable.chat_sender_bg));
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) messageLayout.getLayoutParams();
                params.gravity &= Gravity.END;
                messageLayout.setLayoutParams(params);

            }
            messageTextView.setText(message.getString("text"));
        }

    }

}
