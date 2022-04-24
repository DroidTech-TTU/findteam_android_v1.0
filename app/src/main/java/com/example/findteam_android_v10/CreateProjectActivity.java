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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.findteam_android_v10.adapters.EditTagsAdapter;
import com.example.findteam_android_v10.adapters.GalleryCreateProjectAdapter;
import com.example.findteam_android_v10.classes.Project;
import com.example.findteam_android_v10.fragments.FragMyProjects;
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import co.lujun.androidtagview.TagContainerLayout;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class CreateProjectActivity extends AppCompatActivity {
    private Context context;

    public static String TAG = "CreateProjectActivity";
    public static String CREATE_PROJECT_API_URL = "create";
    public static String SAVE_PICTURE_API_URL = "project/picture?pid=";
    public final static int PICK_PHOTO_CODE = 1046;

    private String message = ""; //Message for popup
    private GalleryCreateProjectAdapter adapter;//Pictures Adapter
    private List<String> picturesURLs;
    private EditText etDescriptionCreateProject;
    private EditText etProjectTitle;
    private List<Bitmap> pictureFiles;

    private RecyclerView rvEditTags;
    private List<String> categories;
    private List<List<String>> tags;
    private EditTagsAdapter editTagsAdapter;



    //On the GUI is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project); //Connect Activity_create_project XML to CreateProjectActivity Controller
        context = this;

        //Connect XML items into this
        pictureFiles = new ArrayList<>();
        etProjectTitle = findViewById(R.id.etProjectTitle);
        etDescriptionCreateProject = findViewById(R.id.etDescriptionCreateProject);
        ImageButton ibSaveCreateProject = findViewById(R.id.ibSaveCreateProject);
        rvEditTags = findViewById(R.id.rvEditTagsCreateProject);
        categories = new ArrayList<>();
        tags = new ArrayList<>();
        editTagsAdapter = new EditTagsAdapter(this, categories, tags);
        rvEditTags.setAdapter(editTagsAdapter);
        rvEditTags.setLayoutManager(new LinearLayoutManager(this));
        rvEditTags.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        FloatingActionButton addEditTag = findViewById(R.id.addEditTagCreateProject);

        //Create an empty gallery
        RecyclerView rvGallery = findViewById(R.id.rvGallery);
        picturesURLs = new ArrayList<String>();
        adapter = new GalleryCreateProjectAdapter(this, picturesURLs);

        //Show warming: "lost data" if cancel, icon(<<)
        ImageButton ibCancel = findViewById(R.id.ibCancel);
        ibCancel.setOnClickListener(this::onButtonShowPopupWindowClick);

        //Add an new empty tags category
        addEditTag.setOnClickListener(view -> {
            categories.add("");//Add an empty Category
            tags.add(new ArrayList<String>());//add ad empty tags belong to this category
            editTagsAdapter.notifyDataSetChanged();//Notify change the tags adapter
        });

        //Save a project
        ibSaveCreateProject.setOnClickListener(view -> {
            try {
                Log.d(TAG, "Save Project Button is on click");
                //If all the inputs are valid, then processing save the project
                if (validateInputs()) {
                    Log.d(TAG, "ibSaveCreateProject: " + message);
                    saveProject(); //Save project
                } else {
                    //Show invalid inputs popup
                    onButtonSavePopupWindowClick(view, message);
                }
            } catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });

        // Attach the adapter to the recyclerview to populate items
        rvGallery.setAdapter(adapter);

        //Set layout manager to position the items
        rvGallery.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));

        //Open the gallery when AddPicture button is on clicked
        ImageButton btAddPicture = findViewById(R.id.btAddPicture);
        btAddPicture.setOnClickListener(this::onPickPhoto);

    }

    //Check if all inputs are valid
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
        if (etDescriptionCreateProject.getText().toString().trim().length() == 0) {
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

    //save project to database and return to my projects page
    private void saveProject() throws JSONException, UnsupportedEncodingException {
        String title = etProjectTitle.getText().toString();//project title
        String description = etDescriptionCreateProject.getText().toString();//project description

        //Project Tags
        JSONArray tagsArray = new JSONArray();
        categories = editTagsAdapter.getCategories();
        tags = editTagsAdapter.getTags();

        Log.i(TAG, "size of categories is: " + categories.size());
        Log.i(TAG, categories.toString());
        Log.i(TAG, "size of tag is: " + tags.size());
        Log.i(TAG, tags.toString());

        //Generate tags to match API's requirement
        for(int i = 0; i < categories.size(); i++){
            for(int j = 0; j < tags.get(i).size(); j++){
                JSONObject tagInstance = new JSONObject();
                tagInstance.put("category", categories.get(i));
                tagInstance.put("text", tags.get(i).get(j));
                tagInstance.put("is_user_requirement", false);
                tagsArray.put(tagInstance);
            }
        }

        //prepare API body
        JSONArray members = new JSONArray();
        JSONObject project = new JSONObject();
        project.put("title", title);
        project.put("status", Project.STATUS_IN_AWAITING_INT);
        project.put("description", description);
        project.put("members", members);
        project.put("tags", tagsArray);

        Log.d(TAG, project.toString());

        //Add API body
        StringEntity entity = new StringEntity(project.toString());

        //Start a POST request
        FindTeamClient.post(this, CREATE_PROJECT_API_URL, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i(TAG, "the status code for this request is: " + statusCode);

                //save Pictures
                int pid = Integer.parseInt(new String(responseBody, StandardCharsets.UTF_8));

                //Save all pictures
                for (Bitmap pic : pictureFiles) {
                    try {
                        //Save one picture
                        savePicture(pid, pic);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                //Navigate back to My Project Pages
                try {
                    Intent i = new Intent();
                    project.put("pid", pid);
                    i.putExtra("project", project.toString());
                    setResult(FragMyProjects.CREATE_PROJECT_CODE, i);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(TAG, "the status code for this request is" + statusCode);
                Log.e(TAG, "" + new String(responseBody));
            }

        });
    }

    //Save Picture
    private void savePicture(int pid, Bitmap pic) throws IOException {

        //Bitmap to byteArray
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        pic.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();

        //Open a new file
        File f = new File(context.getCacheDir(), pic.toString() + ".jpeg");
        f.createNewFile();

        //write binary to jpeg file
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(ba);
        fos.flush();
        fos.close();

        //Prepare for API body
        RequestParams params = new RequestParams();
        params.put("picture", f, "image/jpeg");

        String URL = SAVE_PICTURE_API_URL + pid;//API Save picture request URL
        Log.d(TAG, pic.toString());

        //Processing the POST request for saving picture
        FindTeamClient.post(URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i(TAG, "the status code for this request is: " + statusCode);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(TAG, "the status code for this request is" + statusCode + " " + error);
            }

        });
    }

    // Trigger gallery selection for a photo
    private void onPickPhoto(View view) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Bring up gallery to select a photo
        startActivityForResult(intent, PICK_PHOTO_CODE);
    }

    private Bitmap loadFromUri(Uri photoUri) {
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
            pictureFiles.add(selectedImage);

            // Load the selected image into a preview
            picturesURLs.add(0, photoUri.toString());
            Log.d(TAG, "onActivityResult: PhotoURL = " + photoUri.toString());
            adapter.notifyItemInserted(0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onButtonShowPopupWindowClick(View view) {
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
        // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

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