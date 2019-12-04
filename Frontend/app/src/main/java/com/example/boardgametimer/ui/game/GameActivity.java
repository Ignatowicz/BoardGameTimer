package com.example.boardgametimer.ui.game;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.boardgametimer.R;
import com.example.boardgametimer.data.model.Game;
import com.example.boardgametimer.data.model.LoggedInUser;
import com.example.boardgametimer.data.model.PlayHelper;

import java.util.HashSet;
import java.util.Set;

public class GameActivity extends AppCompatActivity {

    LoggedInUser user;
    Game game;
    PlayHelper play;
    Set<LoggedInUser> friends = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // hide return home button
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);

        this.user = (LoggedInUser) getIntent().getSerializableExtra("user");
        this.game = (Game) getIntent().getSerializableExtra("game");
        this.play = (PlayHelper) getIntent().getSerializableExtra("play");

        System.out.println(play);

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
