package com.example.findteam_android_v10.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.findteam_android_v10.FindTeamClient;
import com.example.findteam_android_v10.R;
import com.example.findteam_android_v10.adapters.MyProjectsAdapter;
import com.example.findteam_android_v10.adapters.ProfilesAdapter;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import cz.msebera.android.httpclient.Header;

public class FragFindUsers extends Fragment {


    public static String TAG = "FragFindUsers";
    public static final String GET_MY_SEARCH = "user/search?query=";
    RecyclerView rvMyProfilesSearch;
    ImageButton btSearchProfiles;
    EditText etSearchProfiles;
    JSONArray jsonProfiles;
    ProfilesAdapter adapter;
    JSONArray usersList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // loads the fragment
        return inflater.inflate(R.layout.frag_find_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //loads items in fragment
        this.rvMyProfilesSearch=view.findViewById(R.id.rvMyProfilesSearch); //get value and assign to variable
        this.btSearchProfiles=view.findViewById(R.id.btSearchProfiles); //same ^
        this.etSearchProfiles=view.findViewById(R.id.etSearchProfiles); //same

        //set up for adapter
        usersList = new JSONArray();
        adapter = new ProfilesAdapter(getContext(), usersList);
        rvMyProfilesSearch.setAdapter(adapter);
        rvMyProfilesSearch.setLayoutManager(new LinearLayoutManager(getActivity()));

        //get data
        String URL = "user/search";//get all users from API/server
        FindTeamClient.get(URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    usersList = new JSONArray(new String(responseBody));
                    adapter.addAll(usersList);
                    Log.d(TAG, "On success: " + usersList.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(TAG, "On success: " + statusCode + "--" + new String(responseBody));
            }
        });

        btSearchProfiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchKey = etSearchProfiles.getText().toString();
                String URL = "user/search?query=" + searchKey;//get all users from API/server
                FindTeamClient.get(URL, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            usersList = new JSONArray(new String(responseBody));
                            adapter.addAll(usersList);
                            Log.d(TAG, "On success: " + usersList.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.d(TAG, "On success: " + statusCode + "--" + new String(responseBody));
                    }
                });
            }
        });
    }
}