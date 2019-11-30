package com.example.boardgametimer.ui.startgame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.boardgametimer.R;
import com.example.boardgametimer.data.model.Game;
import com.example.boardgametimer.data.model.LoggedInUser;

import java.util.ArrayList;

public class StartgameActivity extends AppCompatActivity {
    LoggedInUser user;
    ArrayList<Game> gamesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startgame);

        this.user = (LoggedInUser) getIntent().getSerializableExtra("user");
        this.gamesList = (ArrayList<Game>) getIntent().getSerializableExtra("gamesList");
        int position = getIntent().getIntExtra("position",0);

        System.out.println(gamesList.get(position));



    }
}
