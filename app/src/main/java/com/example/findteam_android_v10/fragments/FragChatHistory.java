package com.example.findteam_android_v10.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.findteam_android_v10.R;

public class FragChatHistory extends Fragment {

    private static final String ARG_UID = "uid";

    private RecyclerView chatListRecyclerView;
    private int uid;

    public FragChatHistory() {
        // Required empty public constructor
    }

/*    public static FragChatHistory newInstance(int uid) {
        FragChatHistory fragment = new FragChatHistory();
        Bundle args = new Bundle();
        args.putInt(ARG_UID, uid);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*        if (getArguments() != null) {
            uid = getArguments().getInt(ARG_UID);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.frag_chat_history, container, false);

        FragChatHistoryArgs args = FragChatHistoryArgs.fromBundle(getArguments());
        uid = args.getUid();

        return view;
    }
}