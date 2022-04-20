package com.example.findteam_android_v10.fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.findteam_android_v10.R;
import com.example.findteam_android_v10.adapters.SearchChatUsersAdapter;
import com.example.findteam_android_v10.classes.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class FragSearchChat extends Fragment {

    public static final String TAG = "FragSearchChat";


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.frag_search_chat, container, false);

        ImageButton searchBtn = view.findViewById(R.id.searchBtn);
        EditText searchBar = view.findViewById(R.id.etSearchBar);
        Toolbar toolbar = view.findViewById(R.id.tbChat);

        toolbar.setNavigationOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();

        });

        JSONArray allUsers = new JSONArray();

        List<String>
                allProfilePics = new ArrayList<>(),
                users = new ArrayList<>(),
                profilePics = new ArrayList<>();
        List<Integer> uids = new ArrayList<>();

        RecyclerView rvSearchChatUsers = view.findViewById(R.id.rvSearchChatUsers);
        SearchChatUsersAdapter searchChatUsersAdapter = new SearchChatUsersAdapter(getContext(), users, profilePics, uids);
        rvSearchChatUsers.setAdapter(searchChatUsersAdapter);
        rvSearchChatUsers.setLayoutManager(new LinearLayoutManager(getContext()));


        //get all the users
        User.getAllUser(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                for(int i = 0; i < response.length(); i++){
                    try {
                        JSONObject user = (JSONObject) response.get(i);
                        String name = user.getString("first_name") + " " +
                                user.getString("middle_name") + " " +
                                user.getString("last_name");
                        String profPic = user.getString("picture");

                        allUsers.put(user);
                        allProfilePics.add(profPic);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.i(TAG, "Error! Did not fetch all the data");
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {

                users.clear();
                profilePics.clear();

                String searchText = searchBar.getText().toString();

                for(int i = 0; i < allUsers.length(); i++){
                    try {
                        JSONObject user = (JSONObject) allUsers.get(i);
                        String name = user.getString("first_name") + " " +
                                user.getString("middle_name") + " " +
                                user.getString("last_name");
                        if(name.toLowerCase().contains(searchText.toLowerCase())){
                            users.add(name);
                            uids.add(user.getInt("uid"));
                            profilePics.add(user.getString("picture"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                searchChatUsersAdapter.notifyDataSetChanged();

            }
        });
        return view;
    }
}