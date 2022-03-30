package com.example.findteam_android_v10;

//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.findteam_android_v10.adapters.galleryCreateProjectAdapter;
import com.example.findteam_android_v10.adapters.myProjectsAdapter;
import com.example.findteam_android_v10.classes.Project;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
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
import java.util.Arrays;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class CreateProjectActivity extends AppCompatActivity {
    Context context;
    RecyclerView rvGallery;
    public static String TAG = "CreateProjectActivity";
    public static String CREATE_PROJECT_API_URL = "create";
    public static String SAVE_PICTURE_API_URL = "project/picture?pid=";
    galleryCreateProjectAdapter adapter;
    Project project;
    List<String> picturesURLs;
    List<String> tagSkills;
    Button btAddPicture;
    ImageButton ibAddTag;
    EditText etTagsCreateProject;
    ImageButton ibSaveCreateProject;
    EditText etProjectTitle;
    ImageButton ibCancel;
    EditText etDescriptionCreateProject;
    TagContainerLayout myProjectsTags;
    List<Bitmap> pictureFiles;
    public static int STATUS = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);
        context = this;
        tagSkills = new ArrayList<>();
        pictureFiles = new ArrayList<>();
        etTagsCreateProject = findViewById(R.id.etTagsCreateProject);
        ibAddTag = findViewById(R.id.ibAddTag);
        etProjectTitle = findViewById(R.id.etProjectTitle);
        etDescriptionCreateProject = findViewById(R.id.etDescriptionCreateProject);
        ibSaveCreateProject = findViewById(R.id.ibSaveCreateProject);
        ibSaveCreateProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Log.d(TAG, "Save Project Button is on click");
                    saveProject();

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

        });
        ibCancel = findViewById(R.id.ibCancel);
        ibCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ibAddTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newTag = etTagsCreateProject.getText().toString();
                Log.d(TAG, newTag);
                tagSkills.add(newTag);
                myProjectsTags.addTag(newTag);
                etTagsCreateProject.setText("");
            }
        });

        myProjectsTags = findViewById(R.id.tgCreateProject);
        myProjectsTags.setTags(tagSkills);
        myProjectsTags.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {

            }

            @Override
            public void onTagLongClick(int position, String text) {
                tagSkills.remove(position);
                myProjectsTags.removeTag(position);
            }

            @Override
            public void onSelectedTagDrag(int position, String text) {

            }

            @Override
            public void onTagCrossClick(int position) {

            }
        });

        this.rvGallery = findViewById(R.id.rvGallery);
        picturesURLs = new ArrayList<String>();
        adapter = new galleryCreateProjectAdapter(this, picturesURLs);
//        // Attach the adapter to the recyclerview to populate items
        rvGallery.setAdapter(adapter);
//        // Set layout manager to position the items
        rvGallery.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));


        btAddPicture = findViewById(R.id.btAddPicture);
        btAddPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickPhoto(view);
            }
        });
    }

    private void saveProject() throws JSONException, UnsupportedEncodingException {
        String title = etProjectTitle.getText().toString();
        String description = etDescriptionCreateProject.getText().toString();

        JSONObject member = new JSONObject();
        member.put("uid", LoginActivity.currentUser.get("uid"));
        member.put("membership_type", 2 );

        JSONArray members = new JSONArray();
        members.put(member);

        JSONArray tagSkillsJSON = new JSONArray();
        for (String skill: tagSkills) {
            JSONObject tag = new JSONObject();
            tag.put("text", skill);
            tag.put("category", "None");
            tag.put("is_user_requirement", false);
            tagSkillsJSON.put(tag);
        }

        JSONObject project = new JSONObject();
        project.put("title", title);
        project.put("status", STATUS);
        project.put("description", description);
        project.put("members", members);
        project.put("tags", tagSkillsJSON);

        Log.d(TAG, project.toString());

        StringEntity entity = new StringEntity(project.toString());
        FindTeamClient.post(this,CREATE_PROJECT_API_URL, entity, new AsyncHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i(TAG, "the status code for this request is: " + statusCode);
                Toast.makeText(context, "Successfully Created Account", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent();

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