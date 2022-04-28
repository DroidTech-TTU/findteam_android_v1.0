package com.example.findteam_android_v10;

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
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.findteam_android_v10.adapters.EditTagsAdapter;
import com.example.findteam_android_v10.adapters.GalleryCreateProjectAdapter;
import com.example.findteam_android_v10.classes.Project;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class UpdateProjectActivity extends AppCompatActivity {
    public static String TAG = "UpdateProjectActivity";
    public static String UPDATE_PROJECT_API_URL = "project?pid=";
    public static String SAVE_PICTURE_API_URL = "project/picture?pid=";
    public final static int PICK_PHOTO_CODE = 1046;
    private Context context;
    private RecyclerView rvGallery;
    private String message = "";
    private GalleryCreateProjectAdapter adapter;
    private ImageButton btAddPicture;
    private ImageButton ibSave;
    private List<String> picturesURLs;
    private List<Bitmap> pictureFiles;
    private EditText etDescription;
    private EditText etProjectTitle;
    private ImageButton ibCancel;
    private ImageView ivStatusUpdateProject;
    private Spinner sProgress;
    private int projectStatus = Project.STATUS_IN_AWAITING_INT;
    private JSONObject project;
    private RecyclerView rvEditTags;
    private List<String> categories;
    private List<List<String>> tags;
    private EditTagsAdapter editTagsAdapter;
    private FloatingActionButton addEditTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_project);
        context = this;

        pictureFiles = new ArrayList<>();
        rvGallery = findViewById(R.id.rvGalleryUpdateProject);
        etProjectTitle = findViewById(R.id.etProjectTitle);
        etDescription = findViewById(R.id.etDescriptionUpdateProject);
        ibSave = findViewById(R.id.ibSaveCreateProject);
        ibCancel = findViewById(R.id.ibCancel);
        sProgress = findViewById(R.id.sProjectProgress);
        rvEditTags = findViewById(R.id.rvEditTagsUpdateProject);
        addEditTag = findViewById(R.id.addEditTagUpdateProject);
        ivStatusUpdateProject = findViewById(R.id.ivStatusUpdateProject);
        btAddPicture = findViewById(R.id.btAddPicture);

        //Get project as a String
        String projectString = this.getIntent().getStringExtra("project");
        try {
            project = new JSONObject(projectString);//Convert to JSONObject
            filloutInterface(project);//bind data to the GUI
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void filloutInterface(JSONObject project) throws JSONException {
        //Show Tags
        categories = new ArrayList<>();
        tags = new ArrayList<>();
        editTagsAdapter = new EditTagsAdapter(this, categories, tags);
        rvEditTags.setAdapter(editTagsAdapter);
        rvEditTags.setLayoutManager(new LinearLayoutManager(this));
        rvEditTags.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        //Add Tag button on click
        addEditTag.setOnClickListener(view -> {
            categories.add("");
            tags.add(new ArrayList<String>());
            editTagsAdapter.notifyDataSetChanged();
        });

        //Show pictures
        picturesURLs = new ArrayList<>();
        JSONArray jsonArray = project.getJSONArray("pictures");
        for (int i = 0; i < jsonArray.length(); i++) {
            picturesURLs.add("https://findteam.2labz.com/picture/" + jsonArray.get(i).toString());
        }
        adapter = new GalleryCreateProjectAdapter(context, picturesURLs);
        // Attach the adapter to the recyclerview to populate items
        rvGallery.setAdapter(adapter);
        // Set layout manager to position the items
        rvGallery.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));

        // Attach the adapter to the recyclerview to populate items
        projectStatus = project.getInt("status");
        ArrayAdapter<CharSequence> sAdapter = ArrayAdapter.createFromResource(this, R.array.progress, R.layout.item_progress_spinner);
        sAdapter.setDropDownViewResource(R.layout.item_progress_spinner);
        sProgress.setAdapter(sAdapter);
        sProgress.setSelection(projectStatus);
        updateImageStatus(projectStatus);
        //Spin progress is on click
        sProgress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateImageStatus(position); //Update project progress icon
                projectStatus = position; //
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //load title
        etProjectTitle.setText(project.getString("title"));
        //load description
        etDescription.setText(project.getString("description"));

        //Add picture button on click
        btAddPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickPhoto(view);
            }
        });

        //Cancel button on click
        ibCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonCancelPopupWindowClick(view);
            }
        });

        //Save button on click
        ibSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //If inputs are valid
                    if (validateInputs()) {
                        upDateProject(); //Update project
                    } else {
                        onButtonSavePopupWindowClick(view, message); //show error popup
                    }
                } catch (JSONException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

        });

        //Show tags
        try {
            JSONArray tagsJson = project.getJSONArray("tags");
            //load the tags of the user
            for(int i = 0; i < tagsJson.length(); i++){
                JSONObject tagObj = (JSONObject) tagsJson.get(i);
                if(!categories.contains(tagObj.getString("category"))){
                    categories.add(tagObj.getString("category"));
                }

            }

            for(int i = 0; i < categories.size(); i++){
                List<String> localTags = new ArrayList<>();
                for(int j = 0; j < tagsJson.length(); j++){
                    JSONObject tagObj = (JSONObject) tagsJson.get(j);
                    if(categories.get(i).equals(tagObj.getString("category"))){
                        localTags.add(tagObj.getString("text"));
                    }
                }
                tags.add(localTags);
            }

            editTagsAdapter.notifyDataSetChanged();
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
    }

    //Validate the inputs
    private boolean validateInputs() {
        boolean isValid = true;
        int count = 1;
        message = "";
        if (picturesURLs == null || picturesURLs.isEmpty()) {
            message = message + count + ".The Images List cannot be empty!\n";
            count++;
            isValid = false;
        }
        if (editTagsAdapter.getTags().size() == 0) {
            message = message + count + ".The Tags List cannot be empty!\n";
            count++;
            isValid = false;
        }
        if (etDescription.getText().toString().trim().length() == 0) {
            message = message + count + ".The Description cannot be empty!\n";
            count++;
            isValid = false;
        }
        if (etProjectTitle.getText().toString().trim().length() == 0) {
            message = message + count + ".The Title cannot be empty!\n";
            count++;
            isValid = false;
        }

        return isValid;
    }


    //Up date project
    private void upDateProject() throws JSONException, UnsupportedEncodingException {
        String title = etProjectTitle.getText().toString();
        String description = etDescription.getText().toString();
        JSONArray tagsArray = new JSONArray();
        categories = editTagsAdapter.getCategories();
        tags = editTagsAdapter.getTags();

        //Convert to tag to API format
        for(int i = 0; i < categories.size(); i++){
            for(int j = 0; j < tags.get(i).size(); j++){
                JSONObject tagInstance = new JSONObject();
                tagInstance.put("category", categories.get(i));
                tagInstance.put("text", tags.get(i).get(j));
                tagInstance.put("is_user_requirement", false);
                tagsArray.put(tagInstance);
            }
        }

        //Add title, status, description, tags to JSONOnject
        project.put("title", title);
        project.put("status", projectStatus);
        project.put("description", description);
        project.put("tags", tagsArray);

        //Get Post request URL
        String URL = UPDATE_PROJECT_API_URL + project.getString("pid");

        //Keep project's data that not required from POST body
        int tmpPid = project.getInt("pid");
        String tmpPics = project.getString("pictures");
        int tmpOwnerId = project.getInt("owner_uid");

        //Remove the not required data out of POST body
        project.remove("pid");
        project.remove("pictures");
        project.remove("owner_uid");

        //Final body
        StringEntity entity = new StringEntity(project.toString());

        //Put back the temporary data
        project.put("pid", tmpPid);
        project.put("pictures", tmpPics);
        project.put("owner_uid", tmpOwnerId);

        //Make a POST request to update project
        FindTeamClient.post(this, URL, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //On success, return to parent activity
                Intent i = new Intent();
                i.putExtra("pid", tmpPid);
                setResult(DetailMyProjectActivity.EDIT_PROJECT_CODE, i);//Add a CODE to let the parent activity know who the child comes back
                savePictures(tmpPid, pictureFiles);
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    Log.i(TAG, "Update(): input : " + entity.getContent().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e(TAG, "Update(): : " + new String(responseBody));
                Log.e(TAG, "Update(): the status code for this request is" + statusCode);
            }

        });
    }

    //Save all pictures
    private void savePictures(int pid, List<Bitmap> pictureFiles) {
        for (Bitmap pic : pictureFiles) {
            try {
                savePicture(pid, pic); //save one picture
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Save one picture to server
    private void savePicture(int pid, Bitmap pic) throws IOException {

        //Get Byte Array
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        pic.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();

        //Create new file
        File f = new File(context.getCacheDir(), pic.toString() + ".jpeg");
        f.createNewFile();

        //write binary to jpeg file
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(ba);
        fos.flush();
        fos.close();

        //Build request parameters
        RequestParams params = new RequestParams();
        params.put("picture", f, "image/jpeg");

        //Concatenate the POST request to save the pictures
        String URL = SAVE_PICTURE_API_URL + pid;

        //Make a POST request
        FindTeamClient.post(URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i(TAG, "update(): the status code for this request is: " + statusCode);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(TAG, "update(): the status code for this request is" + statusCode + " " + error);
                Toast.makeText(context, "Failure to create project", Toast.LENGTH_LONG).show();
            }

        });
    }

    // Trigger gallery selection for a photo
    public void onPickPhoto(View view) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        // Bring up gallery to select a photo
        startActivityForResult(intent, PICK_PHOTO_CODE);
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if (Build.VERSION.SDK_INT > 27) {
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
            if (pictureFiles.isEmpty()) {
                pictureFiles.add(selectedImage);
                // Load the selected image into a preview
                picturesURLs.add(0, photoUri.toString());
                adapter.notifyItemInserted(0);
            } else {
                for (Bitmap b : pictureFiles
                ) {
                    if (!b.sameAs(selectedImage)) {
                        pictureFiles.add(selectedImage);
                        // Load the selected image into a preview
                        picturesURLs.add(0, photoUri.toString());
                        adapter.notifyItemInserted(0);
                    }
                }
            }


        }
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
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

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
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

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

    public void updateImageStatus(int i) {
        Log.d(TAG, "Status ID = " + i);
        switch (i) {
            case Project.STATUS_IN_PROGRESS_INT: {
                ivStatusUpdateProject.setImageResource(R.drawable.ic_project_status_in_progress_green);
                break;
            }
            case Project.STATUS_IN_AWAITING_INT: {
                ivStatusUpdateProject.setImageResource(R.drawable.ic_project_status_in_pending_green);
                break;
            }
            case Project.STATUS_IN_FINISHED_INT: {
                ivStatusUpdateProject.setImageResource(R.drawable.ic_project_status_in_finished_green);
                break;
            }
        }
    }
}