package com.example.boardgametimer.ui.startgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.boardgametimer.R;
import com.example.boardgametimer.data.model.Game;
import com.example.boardgametimer.data.model.LoggedInUser;
import com.example.boardgametimer.ui.game.GameActivity;
import com.example.boardgametimer.ui.main.MainActivity;
import com.example.boardgametimer.ui.newgame.NewGameActivity;

import java.util.ArrayList;

public class StartgameActivity extends AppCompatActivity {
    LoggedInUser user;
    ArrayList<Game> gamesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startgame);

        TextView nameTextView = findViewById(R.id.gameNameTextView);
        TextView playersNumberTextView = findViewById(R.id.playersNumberTextView);
        TextView roundTimeTextView = findViewById(R.id.roundTimeTextView);
        TextView gameTimeTextView = findViewById(R.id.gameTimeTextView);
        Button startGameButton = findViewById(R.id.startGameButton);

        this.user = (LoggedInUser) getIntent().getSerializableExtra("user");
        this.gamesList = (ArrayList<Game>) getIntent().getSerializableExtra("gamesList");
        int position = getIntent().getIntExtra("position",0);

        Game currentGame = gamesList.get(position);

        nameTextView.setText(currentGame.getName());
        playersNumberTextView.setText(Integer.toString(currentGame.getMinPlayers()));
        roundTimeTextView.setText(Integer.toString(currentGame.getTimeRound()));
        gameTimeTextView.setText(Integer.toString(currentGame.getTimeGame()));


        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartgameActivity.this, GameActivity.class);
                intent.putExtra("user",user);
                intent.putExtra("game",gamesList.get(position));
                startActivity(intent);
            }
        });
    }
}
