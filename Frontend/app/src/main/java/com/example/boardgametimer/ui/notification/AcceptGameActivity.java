package com.example.boardgametimer.ui.notification;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.boardgametimer.R;
import com.example.boardgametimer.api.HttpUtils;
import com.example.boardgametimer.data.model.LoggedInUser;
import com.example.boardgametimer.data.model.PlayHelper;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class AcceptGameActivity extends AppCompatActivity {

    LoggedInUser user;
    PlayHelper play;
    String title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_game);

        // hide return home button
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);

        this.user = (LoggedInUser) getIntent().getSerializableExtra("user");
        this.play = (PlayHelper) getIntent().getSerializableExtra("play");
        this.title = (String) getIntent().getSerializableExtra("title");

        TextView textOpenedNotification = findViewById(R.id.textOpenedNotification);
        textOpenedNotification.setText(title);

        final Button acceptGameButton = findViewById(R.id.acceptGameButton);
        final Button rejectGameButton = findViewById(R.id.rejectGameButton);

        acceptGameButton.setOnClickListener(v -> acceptGame());

        rejectGameButton.setOnClickListener(v -> rejectGame());
    }

    private void acceptGame() {
        HttpUtils.get("play/" + play.getPlayId() + "/acceptGame/" + user.getId(), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println(statusCode + response.toString());

                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                System.out.println(statusCode + responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                System.out.println(errorResponse.toString());
            }
        });
    }

    private void rejectGame() {
        HttpUtils.get("play/" + play.getPlayId() + "/rejectGame/" + user.getId(), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println(statusCode + response.toString());

                finishAffinity();
                System.exit(0);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                System.out.println(statusCode + responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                System.out.println(errorResponse.toString());
            }
        });
    }

}
