package com.example.findteam_android_v10.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cz.msebera.android.httpclient.Header;

import com.example.findteam_android_v10.FindTeamClient;
import com.example.findteam_android_v10.MainActivity;
import com.example.findteam_android_v10.R;
import com.example.findteam_android_v10.adapters.ChatListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;

public class FragChatList extends Fragment {

    private RecyclerView chatListRecyclerView;
    private FloatingActionButton fab;
    public  static final String TAG = "FragChatList";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_chat_list, container, false);
        chatListRecyclerView = view.findViewById(R.id.chatListRecyclerView);
        fab = view.findViewById(R.id.newChatButton);
        fab.setOnClickListener(v -> {
            // TODO
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FindTeam", Context.MODE_PRIVATE);
        RequestParams params = new RequestParams();
        params.put("access_token", sharedPreferences.getString("access_token", ""));
        FindTeamClient.get("chats", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                chatListRecyclerView.setAdapter(new ChatListAdapter(getContext(), response));
                chatListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            }
        });
    }
}
