package com.example.boardgametimer.ui.game;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.boardgametimer.R;


public class GameActivity extends AppCompatActivity {

    String title;
    String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_actual_player);

        // hide return home button
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);

        this.title = (String) getIntent().getSerializableExtra("title");
        this.description = (String) getIntent().getSerializableExtra("description");

        TextView okNotification = findViewById(R.id.okNotification);
        okNotification.setText(title);

        TextView okDescriptionNotification = findViewById(R.id.okDescriptionNotification);
        okDescriptionNotification.setText(description);

        final Button okGameButton = findViewById(R.id.okGameButton);

        okGameButton.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "<(\")", Toast.LENGTH_LONG).show();
            finish();
        });
    }

}
