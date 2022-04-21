package com.example.findteam_android_v10.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.findteam_android_v10.FindTeamClient;
import com.example.findteam_android_v10.R;
import com.example.findteam_android_v10.fragments.FragChatListDirections;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import cz.msebera.android.httpclient.Header;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private final JSONArray chats;
    private final Context context;

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
            JSONObject object = chats.getJSONObject(position);
            holder.bind(object.getInt("to_uid"), object.getString("text"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return chats.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView chatListTextView, chatListLastMsg;
        public ImageView chatListImageView;
        public ConstraintLayout layout;

        public ViewHolder(View view) {
            super(view);
            chatListImageView = view.findViewById(R.id.chatListImageView);
            chatListTextView = view.findViewById(R.id.chatListTextView);
            chatListLastMsg = view.findViewById(R.id.chatListLastMsg);

            layout = view.findViewById(R.id.chatListItemLayout);

        }

        public void bind(int toUid, String text) throws JSONException {

            FindTeamClient.get("user?uid=" + toUid, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        StringBuilder fullName = new StringBuilder(response.getString("first_name"));
                        fullName.append(' ');
                        if (!response.getString("middle_name").isEmpty()) {

                            fullName.append(response.getString("middle_name"));
                            fullName.append(' ');

                        }
                        fullName.append(response.getString("last_name"));
                        chatListTextView.setText(fullName.toString());
                        chatListLastMsg.setText(text);
                        Object picture = response.get("picture");
                        if (picture != null) {

                            Glide.with(context)
                                    .load("https://findteam.2labz.com/picture/" + picture)
                                    .into(chatListImageView);

                        }
                        layout.setOnClickListener(v -> {
                            FragChatListDirections.ActionItemChatListToItemChatHistory action = FragChatListDirections.actionItemChatListToItemChatHistory(true, toUid, fullName.toString(), -1);
                            Navigation.findNavController(v).navigate(action);
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

}
