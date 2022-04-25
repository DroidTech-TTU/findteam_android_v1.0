package com.example.findteam_android_v10;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.findteam_android_v10.adapters.EditTagsAdapter;
import com.example.findteam_android_v10.adapters.EditTextUrlsAdapter;
import com.example.findteam_android_v10.classes.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cz.msebera.android.httpclient.Header;

public class EditProfileActivity extends AppCompatActivity {

    //TAG for internal testing
    private static final String TAG = "EditProfileActivity";

    //elements to be extracted/used
    private List<String> urls, categories;
    private List<List<String>> tags;
    private EditText firstName, middleName, lastName;
    private ImageView editProfPic;
    private ProgressDialog dialog;
    private EditTagsAdapter editTagsAdapter;
    private Uri profPicUri;
    private Bitmap bmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //Elements of EditProfile
        urls = new ArrayList<>();
        categories = new ArrayList<>();
        tags = new ArrayList<>();

        //Bind the necessary elements
        editProfPic = findViewById(R.id.editProfPic);

        Button browseImage = findViewById(R.id.profPicSubmitBtn),
                addUrl = findViewById(R.id.urlSubmitBtn);

        firstName = findViewById(R.id.editFirstName);
        middleName = findViewById(R.id.editMiddleName);
        lastName = findViewById(R.id.editLastName);

        EditText url = findViewById(R.id.editAddURL);
        FloatingActionButton addEditTag = findViewById(R.id.addEditTag);

        //set the toolbar
        Toolbar toolbar = findViewById(R.id.tbEditProfile);
        setSupportActionBar(toolbar);

        //setup recyclerview and adapter for urls
        RecyclerView rvUrl = findViewById(R.id.rvEditTextUrls);
        EditTextUrlsAdapter urlAdapter = new EditTextUrlsAdapter(this, urls);
        rvUrl.setAdapter(urlAdapter);
        rvUrl.setLayoutManager(new LinearLayoutManager(this));

        //setup recyclerview and adapter for tags
        RecyclerView rvEditTags = findViewById(R.id.rvEditTags);
        editTagsAdapter = new EditTagsAdapter(this, categories, tags);
        rvEditTags.setAdapter(editTagsAdapter);
        rvEditTags.setLayoutManager(new LinearLayoutManager(this));
        rvEditTags.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        //Button Handler
        browseImage.setOnClickListener(view -> getImageFromGallery.launch("image/*"));

        addUrl.setOnClickListener(view -> {
            urls.add(url.getText().toString());
            url.setText("");
            urlAdapter.notifyDataSetChanged();
        });

        //go back to previous activity
        toolbar.setNavigationOnClickListener(view -> finish());

        //adds a default tag
        addEditTag.setOnClickListener(view -> {
            categories.add("");
            tags.add(new ArrayList<String>());
            editTagsAdapter.notifyDataSetChanged();
        });

        try {

            String picture = LoginActivity.currentUser.getString("picture");
            if(!picture.equals("null")){

                // load the current profile picture if any
                Glide
                        .with(this)
                        .load("https://findteam.2labz.com/picture/" + LoginActivity.currentUser.getString("picture"))
                        .into(editProfPic);

            }


            // load the first name, middle name, and last name
            firstName.setText(LoginActivity.currentUser.getString("first_name"));
            middleName.setText(LoginActivity.currentUser.getString("middle_name"));
            lastName.setText(LoginActivity.currentUser.getString("last_name"));

            //retrieve the urls
            JSONArray urlsJson = LoginActivity.currentUser.getJSONArray("urls");
            for(int i = 0; i < urlsJson.length(); i++){
                JSONObject urlObj = (JSONObject) urlsJson.get(i);
                urls.add("https://" + urlObj.getString("domain") + urlObj.getString("path"));
            }

            urlAdapter.notifyDataSetChanged();

            //retrieve the tags
            JSONArray tagsJson = LoginActivity.currentUser.getJSONArray("tags");

            //load the tags of the user
            for(int i = 0; i < tagsJson.length(); i++){
                JSONObject tagObj = (JSONObject) tagsJson.get(i);
                if(!categories.contains(tagObj.getString("category"))){
                    categories.add(tagObj.getString("category"));
                }

            }

            //retrieve unique categories
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


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    ActivityResultLauncher<String> getImageFromGallery = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    profPicUri = result;

                    //load the selected image from the gallery to the imageView
                    Glide.with(EditProfileActivity.this)
                            .load(result)
                            .into(editProfPic);

                    //extracts the bitmap
                    Glide.with(EditProfileActivity.this)
                            .asBitmap()
                            .load(result)
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    bmap = resource;
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                }
                            });


                }
            });


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(profPicUri != null){

            //changes the profile picture
            User.changeProfilePic(this, bmap, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.i(TAG, "Picture successfully saved. Status code for this request is: " + statusCode);

                    updateUserInfo();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.e(TAG, "the status code for this request is" + statusCode + " " + error);
                }

            });
        } else
            updateUserInfo();

        return true;
    }

    private void updateUserInfo(){

        try{

            //update about info
            JSONObject updateUser = new JSONObject();
            updateUser.put("first_name", firstName.getText().toString());
            updateUser.put("middle_name", middleName.getText().toString());
            updateUser.put("last_name", lastName.getText().toString());
            updateUser.put("email", LoginActivity.currentUser.getString("email"));

            //add the urls
            JSONArray urlsArray = new JSONArray();
            if (!urls.isEmpty()) {
                for (int i = 0; i < urls.size(); i++) {
                    if(!urls.get(i).equals("")) {
                        JSONObject url = new JSONObject();
                        URL urlLink = new URL(urls.get(i));

                        url.put("domain", urlLink.getHost());
                        url.put("path", urlLink.getPath());

                        urlsArray.put(url);
                    }
                }
            }
            updateUser.put("urls", urlsArray);

            //add the tags
            JSONArray tagsArray = new JSONArray();

            categories = editTagsAdapter.getCategories();
            tags = editTagsAdapter.getTags();

            Log.i(TAG, "size of categories is: " + categories.size());
            Log.i(TAG, categories.toString());
            Log.i(TAG, "size of tag is: " + tags.size());
            Log.i(TAG, tags.toString());

            //adds the tag to their corresponding categories
            for(int i = 0; i < categories.size(); i++){
                for(int j = 0; j < tags.get(i).size(); j++){
                    JSONObject tagInstance = new JSONObject();
                    tagInstance.put("category", categories.get(i));
                    tagInstance.put("text", tags.get(i).get(j));

                    tagsArray.put(tagInstance);
                }
            }

            //put the tag into the tag array
            updateUser.put("tags", tagsArray);

            //update the user
            User.updateUser(this, updateUser, new TextHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {

                    Log.i(TAG, "Updating User... the status code for this request is: " + statusCode);

                    User.getCurrentUser(new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                            //go back to the frag profile
                            LoginActivity.currentUser = response;
                            Intent intent = new Intent();
                            setResult(200, intent);
                            finish();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.i(TAG, throwable + " " + errorResponse);
                        }
                    });

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e(TAG, "the status code for this request is: " + statusCode + " " + throwable);
                }

            });

        } catch (JSONException | UnsupportedEncodingException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_profile_menu, menu);
        return true;
    }
}
