package com.example.findteam_android_v10;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.findteam_android_v10.adapters.editTextUrlsAdapter;
import com.example.findteam_android_v10.adapters.urlAdapter;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";

    List<String> urls, locations, skills;
    EditText firstName, middleName, lastName;
    ImageView editProfPic;
    TagContainerLayout locationTagContainer, skillsTagContainer;

    ProgressDialog dialog;

    Uri profPicUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //Elements of EditProfile
        urls = new ArrayList<>();
        locations = new ArrayList<>();
        skills = new ArrayList<>();

        locationTagContainer = findViewById(R.id.editLocationTags);
        skillsTagContainer = findViewById(R.id.editSkillTags);

        editProfPic = findViewById(R.id.editProfPic);

        Button browseImage = findViewById(R.id.profPicSubmitBtn),
                addUrl = findViewById(R.id.urlSubmitBtn),
                addLocation = findViewById(R.id.addLocationBtn),
                addSkill = findViewById(R.id.addSkillBtn);

        firstName = findViewById(R.id.editFirstName);
        middleName = findViewById(R.id.editMiddleName);
        lastName = findViewById(R.id.editLastName);

        EditText url = findViewById(R.id.editAddURL),
                location = findViewById(R.id.editAddLocation),
                skill = findViewById(R.id.editAddSkill);

        //set the toolbar
        Toolbar toolbar = findViewById(R.id.tbEditProfile);
        setSupportActionBar(toolbar);

        //setup recyclerview and adapter
        RecyclerView rvUrl = findViewById(R.id.rvEditTextUrls);
        editTextUrlsAdapter urlAdapter = new editTextUrlsAdapter(this, urls);
        rvUrl.setAdapter(urlAdapter);
        rvUrl.setLayoutManager(new LinearLayoutManager(this));

        //Button Handler
        browseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImageFromGallery.launch("image/*");
            }
        });

        addUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                urls.add(url.getText().toString());
                url.setText("");
                urlAdapter.notifyDataSetChanged();
            }
        });

        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationTagContainer.addTag(location.getText().toString());
                location.setText("");
            }
        });

        addSkill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skillsTagContainer.addTag(skill.getText().toString());
                skill.setText("");
            }
        });



        try {
            //load the current profile picture if any
            Glide.with(this).load("https://findteam.2labz.com/picture/" + LoginActivity.currentUser.getString("picture")).into(editProfPic);


            //load the first name, middle name, and last name
            firstName.setText(LoginActivity.currentUser.getString("first_name"));
            middleName.setText(LoginActivity.currentUser.getString("middle_name"));
            lastName.setText(LoginActivity.currentUser.getString("last_name"));

            //retrieve the urls
            JSONArray urlsJson = LoginActivity.currentUser.getJSONArray("urls");
            for(int i = 0; i < urlsJson.length(); i++){
                JSONObject urlObj = (JSONObject) urlsJson.get(i);
                urls.add(urlObj.getString("domain") + urlObj.getString("path"));
            }
            urlAdapter.notifyDataSetChanged();

            //retrieve the location and skill tags
            JSONArray tagJson = LoginActivity.currentUser.getJSONArray("tags");
            for(int i = 0; i < tagJson.length(); i++){
                JSONObject tagObj = (JSONObject)  tagJson.get(i);
                if(tagObj.getString("category").equals("Location")){
                    locations.add(tagObj.getString("text"));
                } else if(tagObj.getString("category").equals("Skills")){
                    skills.add(tagObj.getString("text"));
                }
            }
            //inflate the location tag container
            locationTagContainer.setTagTypeface(ResourcesCompat.getFont(this, R.font.questrial));
            locationTagContainer.setTags(locations);

            //inflate the skills tag container
            skillsTagContainer.setTagTypeface(ResourcesCompat.getFont(this,R.font.questrial));
            skillsTagContainer.setTags(skills);



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    ActivityResultLauncher<String> getImageFromGallery = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    profPicUri = result;
                    editProfPic.setImageURI(result);
                }
            });


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        dialog = ProgressDialog.show(EditProfileActivity.this, "Loading", "Please Wait", true);

        try{
            //update the profile picture
            if(profPicUri != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                Bitmap map = null;

                map = MediaStore.Images.Media.getBitmap(getContentResolver(), profPicUri);
                File f = new File(getCacheDir(), map.toString() + ".jpeg");
                f.createNewFile();
                map.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] image = stream.toByteArray();

                //write binary to jpeg file
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(image);
                fos.flush();
                fos.close();

                RequestParams params = new RequestParams();
                params.put("picture", f, "image/jpeg");

                FindTeamClient.post("user/picture", params, new AsyncHttpResponseHandler() {

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
                    JSONObject url = new JSONObject();
                    URI uriUrl = new URI("https://" + urls.get(i));

                    Log.i(TAG, uriUrl.getHost() + " " + uriUrl.getPath());

                    url.put("domain", uriUrl.getHost());
                    url.put("path", uriUrl.getPath());

                    urlsArray.put(url);
                }
            }
            updateUser.put("urls", urlsArray);

            //add the tags
            JSONArray tagsArray = new JSONArray();

            if (!locationTagContainer.getTags().isEmpty()) {

                for (int i = 0; i < locationTagContainer.getTags().size(); i++) {
                    JSONObject location = new JSONObject();
                    location.put("text", locationTagContainer.getTags().get(i));
                    location.put("category", "Location");
                    tagsArray.put(location);
                }
            }
            if (!skillsTagContainer.getTags().isEmpty()) {

                for (int i = 0; i < skillsTagContainer.getTags().size(); i++) {
                    JSONObject skill = new JSONObject();
                    skill.put("text", skillsTagContainer.getTags().get(i));
                    skill.put("category", "Skills");
                    tagsArray.put(skill);
                }
            }

            updateUser.put("tags", tagsArray);

            StringEntity entity = new StringEntity(updateUser.toString());
            FindTeamClient.post(this,"user", entity, new TextHttpResponseHandler(){

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Log.i(TAG, "Updating User... the status code for this request is: " + statusCode);

                    //update the user
                    updateUser();

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e(TAG, "the status code for this request is: " + statusCode + " " + throwable);
                }
            });

        } catch (IOException | JSONException | URISyntaxException e) {
            e.printStackTrace();
        }

        return true;
    }

    private void updateUser() {

        FindTeamClient.get("user", new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                LoginActivity.currentUser = response;

                Intent intent = new Intent();
                intent.putExtra("finished", true);
                setResult(200, intent);

                dialog.dismiss();
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, statusCode + " " + responseString + " " + throwable);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_profile_menu, menu);
        return true;
    }
}
