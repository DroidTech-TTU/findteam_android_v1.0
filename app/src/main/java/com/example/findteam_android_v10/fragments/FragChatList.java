package com.example.findteam_android_v10.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.findteam_android_v10.FindTeamClient;
import com.example.findteam_android_v10.R;
import com.example.findteam_android_v10.adapters.ChatListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cz.msebera.android.httpclient.Header;

public class FragChatList extends Fragment {

    private RecyclerView chatListRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_chat_list, container, false);
        chatListRecyclerView = view.findViewById(R.id.chatListRecyclerView);
        FloatingActionButton fab = view.findViewById(R.id.newChatButton);
        fab.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_item_chat_list_to_item_search_chat);
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        FindTeamClient.get("chats", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                chatListRecyclerView.setAdapter(new ChatListAdapter(getContext(), response));
                chatListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                chatListRecyclerView.addItemDecoration(new DividerItemDecoration(chatListRecyclerView.getContext(),
                        DividerItemDecoration.VERTICAL));
            }
        });
    }
}
