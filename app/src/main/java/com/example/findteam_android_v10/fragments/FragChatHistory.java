package com.example.findteam_android_v10.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.findteam_android_v10.FindTeamClient;
import com.example.findteam_android_v10.R;
import com.example.findteam_android_v10.adapters.ChatHistoryAdapter;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class FragChatHistory extends Fragment {

    private final Handler handler = new Handler();
    private RecyclerView chatHistoryRecyclerView;
    private EditText sendMessageEditText;
    private Button sendMessageButton;
    private String title;
    private int puid;
    private boolean isUser;
    private Runnable periodicUpdate = new Runnable() {
        @Override
        public void run() {

            RequestParams params = new RequestParams();
            if (isUser) {

                params.put("uid", puid);

            } else {

                params.put("pid", puid);

            }
            FindTeamClient.get("chat", params, new JsonHttpResponseHandler() {
                @Override
                public void onFinish() {
                    super.onFinish();
                    handler.postDelayed(periodicUpdate, 1000 * 10); // Update chat every 10 seconds
                }
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    chatHistoryRecyclerView.setAdapter(new ChatHistoryAdapter(getContext(), response));
                    chatHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                }
            });
        }
    };

    public FragChatHistory() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragChatHistoryArgs args = FragChatHistoryArgs.fromBundle(getArguments());
        title = args.getTitle();
        puid = args.getPuid();
        isUser = args.getIsUser();
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.post(periodicUpdate);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(periodicUpdate);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_chat_history, container, false);

        Toolbar tbChatHistory = view.findViewById(R.id.tbChatHistory);
        tbChatHistory.setTitle(title);
        tbChatHistory.setNavigationOnClickListener(v -> {
            sendMessageEditText.clearFocus();
            Navigation.findNavController(v).popBackStack();
        });
        chatHistoryRecyclerView = view.findViewById(R.id.chatHistoryRecyclerView);
        sendMessageEditText = view.findViewById(R.id.sendMessageEditText);
        sendMessageButton = view.findViewById(R.id.sendMessageButton);
        sendMessageButton.setOnClickListener(v -> {
            JSONObject message = new JSONObject();
            try {
                message.put("text", sendMessageEditText.getText());
                if (isUser) {

                    message.put("to_uid", puid);

                } else {

                    message.put("to_pid", puid);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                StringEntity entity = new StringEntity(message.toString());
                FindTeamClient.post(getContext(), "chat", entity, new AsyncHttpResponseHandler(){

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                        onResume();

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(getContext(), "Failed to send message", Toast.LENGTH_SHORT).show();
                    }

                });
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            sendMessageEditText.setText("");
        });
        sendMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0) {
                    sendMessageButton.setEnabled(true);
                }
                else {
                    sendMessageButton.setEnabled(false);
                }

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) { }
        });
        return view;
    }
}