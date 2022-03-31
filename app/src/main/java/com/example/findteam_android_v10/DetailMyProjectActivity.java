package com.example.findteam_android_v10;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.findteam_android_v10.adapters.DetailMyProjectAdapter;
import com.example.findteam_android_v10.classes.Project;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;
import cz.msebera.android.httpclient.Header;

public class DetailMyProjectActivity extends AppCompatActivity {
    public static final String GET_PROJECT_API_URL = "project?pid=";
    public static final String STATUS_IN_PROGRESS_STRING = "In Progress";
    public static final int STATUS_IN_PROGRESS_INT = 0;
    public static final String STATUS_FINISHED_STRING = "Finished";
    public static final int STATUS_IN_FINISHED_INT = 1;
    public static final String TAG = "DetailMyProjectActivity";
    RecyclerView rvMembers;
    Context context;
    TextView tvDescription;
    TextView tvProjecTitleDetailProject;
    TextView tvProjectStatus;
    ImageView ivStatus;
    JSONObject project;
    JSONArray members;
    TagContainerLayout tgDetailProject;
    DetailMyProjectAdapter detailMyProjectAdapter;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        getProject();

        setContentView(R.layout.activity_detail_my_project);
        this.rvMembers = (RecyclerView) findViewById(R.id.rvMembers);
        this.tvDescription = (TextView) findViewById(R.id.tvDescriptionMyDetailProjects);
        this.tvProjectStatus = (TextView) findViewById(R.id.tvStatusMyProjectDetailMain);
        this.ivStatus = (ImageView) findViewById(R.id.ivStatusMyProjectDetail) ;
        this.tvProjecTitleDetailProject = findViewById(R.id.tvProjecTitleDetailProject);
        this.tgDetailProject = findViewById(R.id.tgDetailProject);
        try {


            this.tvProjecTitleDetailProject.setText(project.getString("title"));
            this.tvDescription.setText(project.getString("description"));
            String status = project.getInt("status") == STATUS_IN_PROGRESS_INT?STATUS_IN_PROGRESS_STRING:STATUS_FINISHED_STRING;
            this.tvProjectStatus.setText(status);
            List<String> tags = Project.getTagsList(project);
            tgDetailProject.setTags(tags);

            members = project.getJSONArray("members");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getProject(){
        int pid = getIntent().getIntExtra("pid", -1);
        String URL = GET_PROJECT_API_URL + pid;
        FindTeamClient.get(URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    project = new JSONObject(responseBody.toString());
                    Log.i(TAG, "the status code for this request is: " + statusCode);
                    Toast.makeText(context, "Successfully Get Project", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(TAG, "the status code for this request is" + statusCode);
                Toast.makeText(context, "Failure to get project", Toast.LENGTH_LONG).show();
            }
        });
    }

}