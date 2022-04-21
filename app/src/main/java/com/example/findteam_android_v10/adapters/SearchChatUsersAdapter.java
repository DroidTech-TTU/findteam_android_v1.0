package com.example.findteam_android_v10.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.findteam_android_v10.LoginActivity;
import com.example.findteam_android_v10.R;
import com.example.findteam_android_v10.fragments.FragSearchChatDirections;

import org.json.JSONException;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

public class SearchChatUsersAdapter extends RecyclerView.Adapter<SearchChatUsersAdapter.ViewHolder> {

    private final List<String> users;
    private final List<String> profilePics;
    private final List<Integer> uids;

    private final String TAG = "urlAdapter";
    private final Context context;

    public SearchChatUsersAdapter(Context context,List<String> users, List<String> profilePics, List<Integer> uids) {
        this.context = context;
        this.users = users;
        this.profilePics = profilePics;
        this.uids = uids;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_search_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String user = users.get(position);
        String profilePic = profilePics.get(position);
        int uid = uids.get(position);

        setFadeAnimation(holder.itemView);
        holder.bind(user, profilePic, uid);
    }

    private void setFadeAnimation(View view) {

        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(300);
        view.startAnimation(anim);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView profileName;
        public ImageView profilePic;
        public CardView userEntry;

        public ViewHolder(View view) {
            super(view);
            this.profileName = view.findViewById(R.id.searchProfName);
            this.profilePic = view.findViewById(R.id.searchProfPic);
            this.userEntry = view.findViewById(R.id.searchUser);
        }

        public void bind(String user, String pic, int uid) {
            profileName.setText(user);
            if(!pic.equals("")){
                Glide.with(context).load("https://findteam.2labz.com/picture/" + pic).into(profilePic);
            }

            userEntry.setOnClickListener(view -> {

                try {

                    if(uid != LoginActivity.currentUser.getInt("uid")) {
                        FragSearchChatDirections.ActionItemSearchChatToItemChatHistory action = FragSearchChatDirections.actionItemSearchChatToItemChatHistory(true, uid, user, -1);
                        Navigation.findNavController(view).navigate(action);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
