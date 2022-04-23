package com.example.findteam_android_v10.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.findteam_android_v10.CreateProjectActivity;
import com.example.findteam_android_v10.FindTeamClient;
import com.example.findteam_android_v10.LoginActivity;
import com.example.findteam_android_v10.R;
import com.example.findteam_android_v10.adapters.MyProjectsAdapter;
import com.example.findteam_android_v10.classes.Project;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cz.msebera.android.httpclient.Header;

public class FragMyProjects extends Fragment {

    public static String TAG = "FragMyProjects";
    public static final String GET_MY_SEARCH = "project/search";
    public static final int CREATE_PROJECT_CODE = 1122;
    public static final int DELETE_PROJECT_CODE = 1133;
    private RecyclerView rvContacts;
    private FloatingActionButton btCreateProject;
    private EditText etSearchMyProjects;
    private JSONArray jsonProjects;
    private MyProjectsAdapter adapter;

    ActivityResultLauncher<Intent> result = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG, "Data from CreateProject: ");
                    Intent i = result.getData();
                    if ((result.getData() != null) && result.getResultCode() == CREATE_PROJECT_CODE) {
//
                        Log.d(TAG, "Data from CreateProject: " + i.getStringExtra("project"));
                        try {

                            if (jsonProjects.length() > 0) {
                                Log.d(TAG, "Projects has Items: ");
                                int n = jsonProjects.length();
                                for (int j = n - 1; j >= 0; j--) {
                                    jsonProjects.put(j + 1, jsonProjects.getJSONObject(j));
                                }
                            }

                            jsonProjects.put(0, new JSONObject(i.getStringExtra("project")));
                            adapter.addAll(jsonProjects);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    if ((result.getData() != null) && result.getResultCode() == DELETE_PROJECT_CODE) {

                        Log.d(TAG, "On DELETE_PROJECT_CODE: PID=" + i.getStringExtra("pid"));
                    }
                }
            }
    );

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "On create");

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_my_projects, container, false);
        this.rvContacts = (RecyclerView) view.findViewById(R.id.rvMyProjects);
        this.btCreateProject = view.findViewById(R.id.btCreateMyProject);
        this.etSearchMyProjects = view.findViewById(R.id.etSearchMyProjects);

        try {
            getAllProjects();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        rvContacts.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && btCreateProject.isShown())
                    btCreateProject.hide();
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    btCreateProject.show();
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        btCreateProject.setOnClickListener(view1 -> {
            Intent intent = new Intent(view1.getContext(), CreateProjectActivity.class);
            intent.putExtra("requestCode", CREATE_PROJECT_CODE);
            result.launch(intent);
        });

        ImageButton btSearchMyProjects = view.findViewById(R.id.btSearchMyProjects);
        btSearchMyProjects.setOnClickListener(v -> {
            try {
                search();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void getAllProjects() throws JSONException {

        int uid = LoginActivity.currentUser.getInt("uid");
        String URL = GET_MY_SEARCH + "?uid=" + uid;
        Log.d(TAG, "getAllProjects:" + URL);
        FindTeamClient.get(URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i(TAG, "the status code for this request is: " + statusCode);
                try {
                    jsonProjects = new JSONArray(new String(responseBody));
                    Log.i(TAG, "Data: " + jsonProjects);
                    adapter = new MyProjectsAdapter(getContext(), jsonProjects);
                    rvContacts.setAdapter(adapter);//load adapter to recycle list
                    rvContacts.setLayoutManager(new LinearLayoutManager(getActivity()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(TAG, "the status code for this request is" + statusCode);
            }
        });
    }

    public void search() throws JSONException {

        int uid = LoginActivity.currentUser.getInt("uid");

        String URL = GET_MY_SEARCH + "?uid=" + uid;
        Log.d(TAG, "searchEmpty:" + URL);
        FindTeamClient.get(URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i(TAG, "the status code for this request is: " + statusCode);
                try {
                    jsonProjects = new JSONArray(new String(responseBody));
                    Project.printProjects(TAG, jsonProjects);
                    String searchKey = etSearchMyProjects.getText().toString();
                    if (!searchKey.trim().isEmpty()) {
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
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            getAllProjects();
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
    }

}
