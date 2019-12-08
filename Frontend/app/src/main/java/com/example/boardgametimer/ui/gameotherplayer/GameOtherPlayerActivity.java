package com.example.boardgametimer.ui.gameotherplayer;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.boardgametimer.R;
import com.example.boardgametimer.data.model.LoggedInUser;
import com.example.boardgametimer.data.model.PlayHelper;

import java.util.Locale;

public class GameOtherPlayerActivity extends AppCompatActivity {

    LoggedInUser user;
    PlayHelper play;

    long roundTimeLeftInMillis;
    long gameTimeLeftInMillis;

    TextView roundTimerTextView;
    TextView gameTimerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_other_player);

        // hide return home button
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);

        this.user = (LoggedInUser) getIntent().getSerializableExtra("user");
        this.play = (PlayHelper) getIntent().getSerializableExtra("play");

        TextView gameNameTextView = findViewById(R.id.gameNameTextView);
        TextView tourTextView = findViewById(R.id.tourTextView);
        TextView yourGameTimerTextView = findViewById(R.id.yourGameTimerTextView);
        TextView whoseRoundTextView = findViewById(R.id.whoseRoundTextView);

        gameNameTextView.setText(play.getGameName());
        tourTextView.setText(play.isTourA() ? "A" : "B");
        yourGameTimerTextView.setText(setTextGameTimeLeft(play.getGameTimePlayers().get(user.getId())));
        whoseRoundTextView.setText(user.getName());

        roundTimerTextView = findViewById(R.id.roundTimerTextView);
        gameTimerTextView = findViewById(R.id.gameTimerTextView);

        roundTimeLeftInMillis = play.getRoundTimePlayers().get(user.getId()) * 1000;
        gameTimeLeftInMillis = play.getGameTimePlayers().get(user.getId()) * 1000;

        startRoundTimer();
    }

    private String setTextGameTimeLeft(Long gameTime) {
        int minutes = (int) (gameTime / 60);
        int seconds = (int) (gameTime % 60);

        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    private void startRoundTimer() {
        new CountDownTimer(roundTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                roundTimeLeftInMillis = millisUntilFinished;
                updateRoundCountDownText();
            }

            @Override
            public void onFinish() {
                startGameTimer();
            }
        }.start();
    }

    private void updateRoundCountDownText() {
        int minutes = (int) (roundTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (roundTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        roundTimerTextView.setText(timeLeftFormatted);
    }

    private void startGameTimer() {
        new CountDownTimer(gameTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                gameTimeLeftInMillis = millisUntilFinished;
                updateGameCountDownText();
            }

            @Override
            public void onFinish() {
                Toast.makeText(getApplicationContext(), "The time is over!", Toast.LENGTH_LONG).show();
            }
        }.start();
    }

    private void updateGameCountDownText() {
        int minutes = (int) (gameTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (gameTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        gameTimerTextView.setText(timeLeftFormatted);
    }

}
