package com.example.findteam_android_v10;

//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.findteam_android_v10.adapters.GalleryCreateProjectAdapter;
import com.example.findteam_android_v10.classes.Project;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class UpdateProjectActivity extends AppCompatActivity {
    Context context;
    RecyclerView rvGallery;
    public static String TAG = "UpdateProjectActivity";
    public static String UPDATE_PROJECT_API_URL = "update";
    public static String SAVE_PICTURE_API_URL = "project/picture?pid=";
    public String message = "Successfully Create a New Project";
    GalleryCreateProjectAdapter adapter;

    Button btAddPicture;
    ImageButton ibAddTag;
    EditText etTags;
    ImageButton ibSave;
    List<String> picturesURLs;
    List<String> tagSkills;
    EditText etDescription;
    EditText etProjectTitle;
    TextView tvErrorMessage;
    ImageButton ibCancel;

    TagContainerLayout myProjectsTags;
    List<Bitmap> pictureFiles;
    public static int STATUS = 0;
    JSONObject project;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_project);

        context = this;
        tagSkills = new ArrayList<>();
        pictureFiles = new ArrayList<>();
        etTags = findViewById(R.id.etTagsCreateProject);
        ibAddTag = findViewById(R.id.ibAddTag);
        etProjectTitle = findViewById(R.id.etProjectTitle);
        etDescription = findViewById(R.id.etDescriptionCreateProject);
        ibSave = findViewById(R.id.ibSaveCreateProject);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        rvGallery = findViewById(R.id.rvGallery);
        myProjectsTags = findViewById(R.id.tgCreateProject);
        ibCancel = findViewById(R.id.ibCancel);
        ibAddTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newTag = etTags.getText().toString();
                Log.d(TAG, newTag);
                tagSkills.add(newTag);
                myProjectsTags.addTag(newTag);
                etTags.setText("");
            }
        });
        ibCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btAddPicture = findViewById(R.id.btAddPicture);
        btAddPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickPhoto(view);
            }
        });
        String projectString = this.getIntent().getStringExtra("project");
        try {
            project = new JSONObject(projectString);
            Log.d(TAG, "Oncreate: " + projectString);
            filloutInterface(project);
            Log.d(TAG, "Done Fillout:");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ibSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Log.d(TAG, "Save Project Button is on click");
                    if( validateInputs()) {
                        saveProject();
                    }else{

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    private void filloutInterface(JSONObject project) throws JSONException {
        //load images

        picturesURLs = new ArrayList<>();
        JSONArray jsonArray = project.getJSONArray("pictures");
        for(int i = 0; i< jsonArray.length(); i++){
            picturesURLs.add(jsonArray.get(i).toString());
        }
        adapter = new GalleryCreateProjectAdapter(context, picturesURLs);
//        // Attach the adapter to the recyclerview to populate items
        rvGallery.setAdapter(adapter);
//        // Set layout manager to position the items
        rvGallery.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        //load title
        etProjectTitle.setText(project.getString("title"));
        //load description
        etDescription.setText(project.getString("description"));
        //load tags
        tagSkills = Project.getTagsList(project);
        myProjectsTags.setTags(tagSkills);

    }

    private boolean validateInputs() {

        if(picturesURLs == null || picturesURLs.isEmpty()) {
            message = "The Images List cannot be empty!";
            return false;
        }
        if(tagSkills == null || tagSkills.isEmpty()) {
            message = "The Tags List cannot be empty!";
            return false;
        }
        if(etDescription.getText().toString().trim().length() == 0) {
            message = "The Description cannot be empty!";
            return false;
        }
        if(etProjectTitle.getText().toString().trim().length() == 0) {
            message = "The Title cannot be empty!";
            return false;
        }
        message = "Valid Information";
        return true;
    }



    private void saveProject() throws JSONException, UnsupportedEncodingException {
        String title = etProjectTitle.getText().toString();
        String description = etDescription.getText().toString();

        JSONObject member = new JSONObject();
        member.put("uid", LoginActivity.currentUser.get("uid"));
        member.put("membership_type", 2 );

        JSONArray members = new JSONArray();
        members.put(member);

        JSONArray tagSkillsJSON = new JSONArray();
        tagSkills.add(title);
        for (String skill: tagSkills) {
            Log.d(TAG, "SKILL: " + skill);
            JSONObject tag = new JSONObject();
            tag.put("text", skill);
            tag.put("category", "None");
            tag.put("is_user_requirement", false);
            tagSkillsJSON.put(tag);
        }

        project.put("title", title);
        project.put("status", STATUS);
        project.put("description", description);
        project.put("members", members);
        project.put("tags", tagSkillsJSON);

        Log.d(TAG, project.toString());
        String URL = UPDATE_PROJECT_API_URL + "?pid=" + project.getString("pid");
        StringEntity entity = new StringEntity(project.toString());
        FindTeamClient.post(this,UPDATE_PROJECT_API_URL, entity, new AsyncHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i(TAG, "the status code for this request is: " + statusCode);
                Toast.makeText(context, "Successfully Created Account", Toast.LENGTH_SHORT).show();

                //save Pictures
                int pid = Integer.parseInt(new String(responseBody, StandardCharsets.UTF_8));
                Log.d(TAG, String.valueOf(pid));

                for (Bitmap pic: pictureFiles) {
                    try {
                        Log.d(TAG, pic.toString());
                        savePicture(pid, pic);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                Intent i = new Intent();
                Log.d(TAG, "Back to DetailMyProject:" + project.toString());
                i.putExtra("project", project.toString());
                setResult(DetailMyProjectActivity.EDIT_PROJECT_CODE, i);
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(TAG, "the status code for this request is" + statusCode);
                Toast.makeText(context, "Failure to create project", Toast.LENGTH_LONG).show();
            }

        });
    }

    private void savePicture(int pid, Bitmap pic) throws IOException {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        pic.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();

        File f = new File(context.getCacheDir(), pic.toString()+".jpeg");
        f.createNewFile();

        //write binary to jpeg file
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(ba);
        fos.flush();
        fos.close();

        RequestParams params = new RequestParams();
        params.put("picture", f, "image/jpeg");

        String URL = SAVE_PICTURE_API_URL + pid;
        Log.d(TAG, pic.toString());
        FindTeamClient.post(URL, params , new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i(TAG, "the status code for this request is: " + statusCode);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(TAG, "the status code for this request is" + statusCode + " " + error);
                Toast.makeText(context, "Failure to create project", Toast.LENGTH_LONG).show();
            }

        });
    }

    // PICK_PHOTO_CODE is a constant integer
    public final static int PICK_PHOTO_CODE = 1046;

    // Trigger gallery selection for a photo
    public void onPickPhoto(View view) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if(Build.VERSION.SDK_INT > 27){
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            Uri photoUri = data.getData();
            // Load the image located at photoUri into selectedImage
            Bitmap selectedImage = loadFromUri(photoUri);
            if(pictureFiles.isEmpty()){
                pictureFiles.add(selectedImage);
                // Load the selected image into a preview
                picturesURLs.add(0, photoUri.toString());
                adapter.notifyItemInserted(0);
            }else{
                for (Bitmap b: pictureFiles
                ) {
                    if(!b.sameAs(selectedImage)){
                        pictureFiles.add(selectedImage);
                        // Load the selected image into a preview
                        picturesURLs.add(0, photoUri.toString());
                        adapter.notifyItemInserted(0);
                    }
                }
            }



        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}