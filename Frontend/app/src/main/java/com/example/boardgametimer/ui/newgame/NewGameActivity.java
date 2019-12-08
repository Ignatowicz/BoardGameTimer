package com.example.boardgametimer.ui.newgame;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.boardgametimer.R;
import com.example.boardgametimer.api.HttpUtils;
import com.example.boardgametimer.data.model.Game;
import com.example.boardgametimer.data.model.LoggedInUser;
import com.example.boardgametimer.ui.main.MainActivity;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class NewGameActivity extends AppCompatActivity {

    LoggedInUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        // hide return home button
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);

        this.user = (LoggedInUser) getIntent().getSerializableExtra("user");

        final Button startGameButton = findViewById(R.id.start_game_button);
        EditText nameEditText = findViewById(R.id.nameEditText);
        EditText minPlayersEditText = findViewById(R.id.minPlayersEditText);
        EditText maxPlayersEditText = findViewById(R.id.maxPlayersEditText);
        EditText timeRoundEditText = findViewById(R.id.timeRoundEditText);
        EditText timeGameEditText = findViewById(R.id.timeGameEditText);

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (
                        nameEditText.getText().toString().isEmpty() ||
                                minPlayersEditText.getText().toString().isEmpty() ||
                                maxPlayersEditText.getText().toString().isEmpty() ||
                                timeRoundEditText.getText().toString().isEmpty() ||
                                timeGameEditText.getText().toString().isEmpty() ||
                                Integer.valueOf(minPlayersEditText.getText().toString()) > Integer.valueOf(maxPlayersEditText.getText().toString())
                ) {
                    startGameButton.setEnabled(false);
                } else {
                    startGameButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        nameEditText.addTextChangedListener(watcher);
        minPlayersEditText.addTextChangedListener(watcher);
        maxPlayersEditText.addTextChangedListener(watcher);
        timeRoundEditText.addTextChangedListener(watcher);
        timeGameEditText.addTextChangedListener(watcher);

        startGameButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            Integer minPlayers = Integer.valueOf(minPlayersEditText.getText().toString());
            Integer maxPlayers = Integer.valueOf(maxPlayersEditText.getText().toString());
            Integer timeRound = Integer.valueOf(timeRoundEditText.getText().toString());
            Integer timeGame = Integer.valueOf(timeGameEditText.getText().toString());

            Game game = new Game(name, minPlayers, maxPlayers, timeRound, timeGame);
            user.getGames().add(game);
            addGameToUser();
        });
    }

    public void addGameToUser() {
        Gson gson = new Gson();
        String jsonParams = gson.toJson(user);
        final StringEntity[] entity;
        try {
            entity = new StringEntity[]{new StringEntity(jsonParams)};
            HttpUtils.put(getApplicationContext(), "players/" + user.getId(), entity[0], "application/json", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    System.out.println(statusCode + response.toString());

                    Gson gson = new Gson();
                    JsonElement element = gson.fromJson(response.toString(), JsonElement.class);
                    LoggedInUser addedUser = gson.fromJson(element, LoggedInUser.class);

                    Intent intent = new Intent(NewGameActivity.this, MainActivity.class);
                    intent.putExtra("user", addedUser);
                    setResult(RESULT_OK, intent);
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

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("user", user);
        setResult(RESULT_OK, intent);
        finish();
    }

}

