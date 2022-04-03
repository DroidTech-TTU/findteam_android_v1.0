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
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.ClientCertRequest;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.findteam_android_v10.Utils.CountDown;
import com.example.findteam_android_v10.adapters.GalleryCreateProjectAdapter;
import com.example.findteam_android_v10.classes.Picture;
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
    public static String UPDATE_PROJECT_API_URL = "project?pid=";
    public static String SAVE_PICTURE_API_URL = "project/picture?pid=";
    public String message ="";
    GalleryCreateProjectAdapter adapter;

    ImageButton btAddPicture;
    ImageButton ibAddTag;
    EditText etTags;
    ImageButton ibSave;
    List<String> picturesURLs;
    List<Bitmap> pictureFiles;
    List<String> tagSkills;
    EditText etDescription;
    EditText etProjectTitle;
    ImageButton ibCancel;

    TagContainerLayout myProjectsTags;
    public static int STATUS = 0;
    JSONObject project;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_project);

        context = this;
        pictureFiles = new ArrayList<>();
        rvGallery = findViewById(R.id.rvGalleryUpdateProject);
        etTags = findViewById(R.id.etTagsUpdateProject);
        ibAddTag = findViewById(R.id.ibAddTag);
        etProjectTitle = findViewById(R.id.etProjectTitle);
        etDescription = findViewById(R.id.etDescriptionCreateProject);
        ibSave = findViewById(R.id.ibSaveCreateProject);
        myProjectsTags = findViewById(R.id.tgCreateProject);
        ibCancel = findViewById(R.id.ibCancel);

        btAddPicture = findViewById(R.id.btAddPicture);

        String projectString = this.getIntent().getStringExtra("project");
        try {
            project = new JSONObject(projectString);
            Log.d(TAG, "Oncreate: " + projectString);
            filloutInterface(project);
            Log.d(TAG, "Done Fillout:");

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void filloutInterface(JSONObject project) throws JSONException {
        //load images

        picturesURLs = new ArrayList<>();
        JSONArray jsonArray = project.getJSONArray("pictures");
        for(int i = 0; i< jsonArray.length(); i++){
            picturesURLs.add(Picture.GET_PICTURE_URL +jsonArray.get(i).toString());
        }
        adapter = new GalleryCreateProjectAdapter(context, picturesURLs);
//        // Attach the adapter to the recyclerview to populate items
        rvGallery.setAdapter(adapter);
//        // Set layout manager to position the items
        rvGallery.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));

//        adapter.addAll(picturesURLs);
//        // Attach the adapter to the recyclerview to populate items

        //load title
        etProjectTitle.setText(project.getString("title"));
        //load description
        etDescription.setText(project.getString("description"));
        //load tags
        tagSkills = Project.getTagsList(project);
        myProjectsTags.setTags(tagSkills);

        btAddPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickPhoto(view);
            }
        });

        ibAddTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newTag = etTags.getText().toString();
                Log.d(TAG, "New Tag = " + newTag);
                tagSkills.add(newTag);
                myProjectsTags.addTag(newTag);
                etTags.setText("");
            }
        });
        ibCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonCancelPopupWindowClick(view);
            }
        });
        ibSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Log.d(TAG, "Save Project Button is on click");
                    if( validateInputs()) {
                        saveProject();
                    }else{
                        onButtonSavePopupWindowClick(view, message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    private boolean validateInputs() {
        boolean isValid = true;
        int count = 1;
        message="";
        if(picturesURLs == null || picturesURLs.isEmpty()) {
            message = message + count+".The Images List cannot be empty!\n";
            count++;
            isValid = false;
        }
        if(tagSkills == null || tagSkills.isEmpty()) {
            message = message + count+".The Tags List cannot be empty!\n";
            count++;
            isValid = false;
        }
        if(etDescription.getText().toString().trim().length() == 0) {
            message = message + count+".The Description cannot be empty!\n";
            count++;
            isValid = false;
        }
        if(etProjectTitle.getText().toString().trim().length() == 0) {
            message =message +  count+".The Title cannot be empty!\n";
            count++;
            isValid = false;
        }

        return isValid;
    }



    private void saveProject() throws JSONException, UnsupportedEncodingException {
        String title = etProjectTitle.getText().toString();
        String description = etDescription.getText().toString();

        JSONObject member = new JSONObject();
        member.put("uid", LoginActivity.currentUser.get("uid"));
        member.put("membership_type", 0);
        JSONArray members = new JSONArray();
        members.put(member);

        JSONArray tagSkillsJSON = new JSONArray();
//        tagSkills.add(title);
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
        String URL = UPDATE_PROJECT_API_URL + project.getString("pid");
        int tmpPid = project.getInt("pid");
        String tmpPics = project.getString("pictures");

        project.remove("pid");
        project.remove("pictures");
        project.remove("owner_uid");
        StringEntity entity = new StringEntity(project.toString());

        FindTeamClient.post(this,URL, entity, new AsyncHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i(TAG, "saveProject(): the status code for this request is: " + statusCode);
                Toast.makeText(context, "Successfully Created Account", Toast.LENGTH_SHORT).show();

                Intent i = new Intent();
                i.putExtra("pid", tmpPid);
                setResult(DetailMyProjectActivity.EDIT_PROJECT_CODE, i);
                savePictures(tmpPid, pictureFiles);
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(TAG, "saveProject(): project: " + project.toString());
                Log.e(TAG, "saveProject(): the status code for this request is" + statusCode);
                Toast.makeText(context, "Failure to create project", Toast.LENGTH_LONG).show();
            }

        });
    }
    private void savePictures(int pid, List<Bitmap> pictureFiles){
        for (Bitmap pic: pictureFiles) {
            try {
                Log.d(TAG, pic.toString());
                savePicture(pid, pic);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
                Log.i(TAG, "savePicture(): the status code for this request is: " + statusCode);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(TAG, "savePicture(): the status code for this request is" + statusCode + " " + error);
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

    public void onButtonCancelPopupWindowClick(View view) {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_cancel_create_project, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView,width,height,focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return false;
            }
        });
        Button btYesCancelProject = popupView.findViewById(R.id.btYesCancelProject);
        Button btNoCancelProject = popupView.findViewById(R.id.btNoCancelProject);

        btYesCancelProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                finish();
            }
        });
        btNoCancelProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    public void onButtonSavePopupWindowClick(View view, String message) {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_invalid_inputs_create_project, null);
        TextView tvPopup_Invalid_Inputs = popupView.findViewById(R.id.tvPopup_Invalid_Inputs);
        tvPopup_Invalid_Inputs.setText(message);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView,width,height,focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }
}