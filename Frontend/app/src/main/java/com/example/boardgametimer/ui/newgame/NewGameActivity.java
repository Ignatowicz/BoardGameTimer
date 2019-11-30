package com.example.boardgametimer.ui.newgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.boardgametimer.R;
import com.example.boardgametimer.api.HttpUtils;
import com.example.boardgametimer.data.Result;
import com.example.boardgametimer.data.model.Game;
import com.example.boardgametimer.data.model.LoggedInUser;
import com.example.boardgametimer.ui.game.GameActivity;
import com.example.boardgametimer.ui.main.MainActivity;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class NewGameActivity extends AppCompatActivity {
    LoggedInUser user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
        this.user = (LoggedInUser) getIntent().getSerializableExtra("user");

        final Button startGameButton = findViewById(R.id.start_game_button);
        EditText nameEditText = findViewById(R.id.nameEditText);
        EditText minPlayersEditText = findViewById(R.id.minPlayersEditText);
        EditText maxPlayersEditText = findViewById(R.id.maxPlayersEditText);
        EditText timeRoundEditText = findViewById(R.id.timeRoundEditText);
        EditText timeGameEditText = findViewById(R.id.timeGameEditText);

        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString();
                Integer minPlayers = Integer.valueOf(minPlayersEditText.getText().toString());
                Integer maxPlayers = Integer.valueOf(maxPlayersEditText.getText().toString());
                Integer timeRound = Integer.valueOf(timeRoundEditText.getText().toString());
                Integer timeGame = Integer.valueOf(timeGameEditText.getText().toString());

                Game game = new Game(name, minPlayers, maxPlayers, timeRound, timeGame);
                addGame(game);

            }
        });
    }

        public void addUser(){
            Gson gson = new Gson();
            String jsonParams = gson.toJson(user);
            final StringEntity[] entity;
            try {
                entity = new StringEntity[]{new StringEntity(jsonParams)};
                HttpUtils.put(getApplicationContext(), "players/" + user.getId(), entity[0],"application/json", new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Gson gson = new Gson();
                        JsonElement element =  gson.fromJson(response.toString(), JsonElement.class);
                        LoggedInUser addedUser = gson.fromJson(element, LoggedInUser.class);

                        Intent intent = new Intent(NewGameActivity.this, MainActivity.class);
                        intent.putExtra("user",user);
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

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        public void addGame(Game game){
            Gson gson = new Gson();
            String jsonParams = gson.toJson(game);
            final StringEntity[] entity;
            try {
                entity = new StringEntity[]{new StringEntity(jsonParams)};
                HttpUtils.post(getApplicationContext(), "games/add", entity[0], "application/json", new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Gson gson = new Gson();
                        JsonElement element = gson.fromJson(response.toString(), JsonElement.class);
                        Game addedGame = gson.fromJson(element, Game.class);
                        user.addGame(addedGame);
                        addUser();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        System.out.println(statusCode + responseString);
                    }
                });
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
    }

