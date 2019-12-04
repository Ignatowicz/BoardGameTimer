package com.example.boardgametimer.ui.startgame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.boardgametimer.R;
import com.example.boardgametimer.api.HttpUtils;
import com.example.boardgametimer.data.model.Game;
import com.example.boardgametimer.data.model.LoggedInUser;
import com.example.boardgametimer.ui.game.GameActivity;
import com.example.boardgametimer.ui.main.MainActivity;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class StartgameActivity extends AppCompatActivity {

    LoggedInUser user;
    List<Game> games = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startgame);

        // hide return home button
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);

        TextView nameTextView = findViewById(R.id.gameNameTextView);
        TextView playersNumberTextView = findViewById(R.id.playersNumberTextView);
        TextView roundTimeTextView = findViewById(R.id.roundTimeTextView);
        TextView gameTimeTextView = findViewById(R.id.gameTimeTextView);
        Button startGameButton = findViewById(R.id.startGameButton);

        this.user = (LoggedInUser) getIntent().getSerializableExtra("user");
        this.games = (ArrayList<Game>) getIntent().getSerializableExtra("games");
        int position = getIntent().getIntExtra("position", 0);

        Game currentGame = games.get(position);

        nameTextView.setText(currentGame.getName());
        playersNumberTextView.setText(Integer.toString(currentGame.getMinPlayers()));
        roundTimeTextView.setText(Integer.toString(currentGame.getTimeRound()));
        gameTimeTextView.setText(Integer.toString(currentGame.getTimeGame()));


        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartgameActivity.this, GameActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("game", games.get(position));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("user", user);
        setResult(RESULT_OK, intent);
        finish();
    }
}
