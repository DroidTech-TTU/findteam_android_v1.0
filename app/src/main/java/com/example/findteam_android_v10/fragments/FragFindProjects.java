package com.example.findteam_android_v10.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findteam_android_v10.FindTeamClient;
import com.example.findteam_android_v10.R;
import com.example.findteam_android_v10.adapters.MyProjectsAdapter;
import com.example.findteam_android_v10.classes.Project;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import cz.msebera.android.httpclient.Header;

public class FragFindProjects extends Fragment {

    public static String TAG = "FragFindProjects";
    public static final String GET_MY_SEARCH = "project/search?query=";
    public static final int CREATE_PROJECT_CODE = 1133;
    RecyclerView rvContacts;
    ImageButton btSearchMyProjects;
    EditText etSearchMyProjects;
    JSONArray jsonProjects;
    MyProjectsAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "On create");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_find_projects, container, false);
        this.rvContacts = (RecyclerView) view.findViewById(R.id.rvMyProjectsSearch);
        this.btSearchMyProjects = view.findViewById(R.id.btSearchMyProjects);
        this.etSearchMyProjects = view.findViewById(R.id.etSearchMyProjects);

        try {
            getAllProjects();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        this.btSearchMyProjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    search();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void getAllProjects() throws JSONException {
        String URL = GET_MY_SEARCH;
        Log.d(TAG,"getAllProjects:" + URL );
        FindTeamClient.get(URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i(TAG, "the status code for this request is: " + statusCode );
                try {
                    jsonProjects = new JSONArray(new String(responseBody));
                    Log.i(TAG, "Data: " + jsonProjects);
                    adapter = new MyProjectsAdapter(getContext(), jsonProjects);
                    rvContacts.setAdapter(adapter);
                    rvContacts.setLayoutManager(new LinearLayoutManager(getActivity()));
                    Toast.makeText(getContext(), "Successfully Get Projects:", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(TAG, "the status code for this request is" + statusCode);
                Toast.makeText(getContext(), "Get Projects Failure: ", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void search() throws JSONException {

        String URL = GET_MY_SEARCH + etSearchMyProjects.getText();
        Log.d(TAG,"searchEmpty:" + URL );
        FindTeamClient.get(URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i(TAG, "the status code for this request is: " + statusCode );
                try {
                    jsonProjects = new JSONArray(new String(responseBody));
                    Log.i(TAG, "Data: " + jsonProjects);
                    Project.printProjects(TAG, jsonProjects);
                    String searchKey = etSearchMyProjects.getText().toString();
                    if(!searchKey.trim().isEmpty()){
                        jsonProjects = Project.search(jsonProjects, searchKey);


                    }
                    Log.i(TAG, "Search Results: " + jsonProjects);
                    Project.printProjects(TAG, jsonProjects);
                    adapter.clear();
                    adapter.addAll(jsonProjects);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(TAG, "the status code for this request is" + statusCode);
                Toast.makeText(getContext(), "Search Projects Failure: ", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
