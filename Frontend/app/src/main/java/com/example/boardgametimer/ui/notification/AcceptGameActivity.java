package com.example.boardgametimer.ui.notification;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.boardgametimer.R;
import com.example.boardgametimer.api.HttpUtils;
import com.example.boardgametimer.data.model.LoggedInUser;
import com.example.boardgametimer.ui.game.GameActivity;
import com.example.boardgametimer.ui.main.MainActivity;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class AcceptGameActivity extends AppCompatActivity {

    String playerId;
    String playId;
    String title;
    LoggedInUser user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_game);

        this.playerId = (String) getIntent().getSerializableExtra("playerId");
        this.playId = (String) getIntent().getSerializableExtra("playId");
        this.title = (String) getIntent().getSerializableExtra("title");

        final TextView textOpenedNotification = findViewById(R.id.textOpenedNotification);
        textOpenedNotification.setText(title);

        final Button acceptGameButton = findViewById(R.id.acceptGameButton);
        final Button rejectGameButton = findViewById(R.id.rejectGameButton);

        getActualPlayer();

        acceptGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptGame();
            }
        });

        rejectGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectGame();
            }
        });
    }

    private void getActualPlayer() {
        HttpUtils.get("players/" + playerId, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Gson gson = new Gson();
                JsonElement element = gson.fromJson(response.toString(), JsonElement.class);
                user = gson.fromJson(element, LoggedInUser.class);
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

    private void acceptGame() {
        HttpUtils.get("play/" + playId + "/acceptGame/" + playerId, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Intent intent = new Intent(AcceptGameActivity.this, GameActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
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
        HttpUtils.get("play/" + playId + "/rejectGame/" + playerId, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Intent intent = new Intent(AcceptGameActivity.this, MainActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
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
