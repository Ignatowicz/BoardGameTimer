package com.example.boardgametimer.ui.newgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.boardgametimer.R;
import com.example.boardgametimer.ui.game.GameActivity;

public class NewGameActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
        final Button startGameButton = findViewById(R.id.start_game_button);


        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewGameActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });
    }
}
