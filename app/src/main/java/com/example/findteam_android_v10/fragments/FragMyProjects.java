package com.example.findteam_android_v10.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findteam_android_v10.CreateProjectActivity;
import com.example.findteam_android_v10.FindTeamClient;
import com.example.findteam_android_v10.LoginActivity;
import com.example.findteam_android_v10.R;
import com.example.findteam_android_v10.adapters.MyProjectsAdapter;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class FragMyProjects extends Fragment{
    public static String TAG = "FragMyProjects";
    public static final String GET_MY_SEARCH = "project/search";
    public static final int CREATE_PROJECT_CODE = 1122;
    RecyclerView rvContacts;
    View btCreateProject;
    ImageButton btSearchMyProjects;
    EditText etSearchMyProjects;
    JSONArray jsonProjects;
    MyProjectsAdapter adapter;

    ActivityResultLauncher<Intent> result = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG, "Data from CreateProject: ");
                    Intent i  = result.getData();
                    if ((result.getData() != null) && result.getResultCode() == CREATE_PROJECT_CODE) {
//
                        Log.d(TAG, "Data from CreateProject: " + i.getStringExtra("project"));
                        try {
                            JSONObject js = new JSONObject(i.getStringExtra("project"));
                            jsonProjects.put(0, js);
                            adapter.notifyItemInserted(0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
    );

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "On create");

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }

    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_my_projects, container, false);
        this.rvContacts = (RecyclerView) view.findViewById(R.id.rvMyProjectsSearch);
        this.btCreateProject = view.findViewById(R.id.btCreateMyProject);
        this.btSearchMyProjects = view.findViewById(R.id.btSearchMyProjects);
        this.etSearchMyProjects = view.findViewById(R.id.etSearchMyProjects);

        try {
            getAllProjects();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btCreateProject.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CreateProjectActivity.class);
                intent.putExtra("requestCode", CREATE_PROJECT_CODE);
                result.launch(intent);
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

        String URL = GET_MY_SEARCH +"?uid=" + uid;
        Log.d(TAG,"getAllProjects:" + URL );
        FindTeamClient.get(URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i(TAG, "the status code for this request is: " + statusCode );

                try {
                    jsonProjects = new JSONArray(new String(responseBody));
                    Log.i(TAG, "Data: " + jsonProjects);
                    //       Log.d(TAG, "OncreateView: " + jsonProjects.toString());
                    adapter = new MyProjectsAdapter(getContext(), jsonProjects);
                    // Attach the adapter to the recyclerview to populate items
                    rvContacts.setAdapter(adapter);
                    // Set layout manager to position the items
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


    public void searchProjects() {
       JSONArray searchProjects = new JSONArray();
        for (int i=0; i < this.jsonProjects.length(); i++  ) {

        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
