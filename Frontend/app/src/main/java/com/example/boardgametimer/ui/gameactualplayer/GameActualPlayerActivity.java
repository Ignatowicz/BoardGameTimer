package com.example.boardgametimer.ui.gameactualplayer;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.boardgametimer.R;
import com.example.boardgametimer.api.HttpUtils;
import com.example.boardgametimer.data.model.LoggedInUser;
import com.example.boardgametimer.data.model.PlayHelper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class GameActualPlayerActivity extends AppCompatActivity {

    LoggedInUser user;
    PlayHelper play;

    Button endGameButton;
    Button endTourButton;
    Button endRoundButton;
    Button pauseGameButton;

    TextView roundTimerTextView;
    TextView gameTimerTextView;
    private CountDownTimer roundCountDownTimer;
    private CountDownTimer gameCountDownTimer;
    long roundTimeLeftInMillis;
    long gameTimeLeftInMillis;

    Boolean isPaused = false;
    Boolean timeRoundHasEnded = false;

    TextView gameNameTextView;
    TextView whoseRoundTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_actual_player);

        // hide return home button
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);

        this.user = (LoggedInUser) getIntent().getSerializableExtra("user");
        this.play = (PlayHelper) getIntent().getSerializableExtra("play");

        gameNameTextView = findViewById(R.id.gameNameTextView);
        whoseRoundTextView = findViewById(R.id.whoseRoundTextView);

        gameNameTextView.setText(play.getGameName());
        whoseRoundTextView.setText(user.getName());

        roundTimerTextView = findViewById(R.id.roundTimerTextView);
        gameTimerTextView = findViewById(R.id.gameTimerTextView);

        roundTimeLeftInMillis = play.getRoundTimePlayers().get(user.getId()) * 1000;
        gameTimeLeftInMillis = play.getGameTimePlayers().get(user.getId()) * 1000;

        endGameButton = findViewById(R.id.endGameButton);
        endTourButton = findViewById(R.id.endTourButton);
        endRoundButton = findViewById(R.id.endRoundButton);
        pauseGameButton = findViewById(R.id.pauseGameButton);

        endGameButton.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Exit this view will provoke cancellation the game")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, (arg0, arg1) -> endGame()).create().show());

        endTourButton.setOnClickListener(v -> endTour());

        endRoundButton.setOnClickListener(v -> endRound());

        pauseGameButton.setOnClickListener(v -> pauseOrResumeGame());

        startRoundTimer();
        updateGameCountDownText();
    }

    private void endRound() {
        HttpUtils.get("play/" + play.getPlayId() + "/endRound/" + user.getId(), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println(statusCode + response.toString());

                Gson gson = new Gson();
                JsonElement element = gson.fromJson(response.toString(), JsonElement.class);
                PlayHelper play = gson.fromJson(element, PlayHelper.class);

                play.getGameTimePlayers().put(user.getId(), gameTimeLeftInMillis / 1000);

                updatePlay(play);
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

    private void updatePlay(PlayHelper play) {
        Gson gson = new Gson();
        String jsonParams = gson.toJson(play);
        final StringEntity[] entity;
        try {
            entity = new StringEntity[]{new StringEntity(jsonParams)};
            HttpUtils.put(getApplicationContext(), "play/" + play.getPlayId(), entity[0], "application/json", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    System.out.println(statusCode + response.toString());

                    endGameButton.setEnabled(false);
                    endTourButton.setEnabled(false);
                    endRoundButton.setEnabled(false);
                    pauseGameButton.setEnabled(false);

                    pauseOrResumeGame();

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

    private void endTour() {
        HttpUtils.get("play/" + play.getPlayId() + "/endTour/" + user.getId(), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println(statusCode + response.toString());

                Gson gson = new Gson();
                JsonElement element = gson.fromJson(response.toString(), JsonElement.class);
                PlayHelper play = gson.fromJson(element, PlayHelper.class);

                play.getGameTimePlayers().put(user.getId(), gameTimeLeftInMillis / 1000);

                updatePlay(play);
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

    private void endGame() {
        HttpUtils.get("play/" + play.getPlayId() + "/endGame/" + user.getId(), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println(statusCode + response.toString());

                pauseOrResumeGame();

                Toast.makeText(getApplicationContext(), "Bye!", Toast.LENGTH_LONG).show();

                finishAffinity();
                System.exit(0);
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

    private void pauseOrResumeGame() {
        isPaused = !isPaused;

        if (isPaused) {
            pauseGameButton.setText("Resume");
            endGameButton.setEnabled(false);
            endTourButton.setEnabled(false);
            endRoundButton.setEnabled(false);
            if (timeRoundHasEnded) {
                pauseGameTimer();
            } else {
                pauseRoundTimer();
            }
        } else {
            pauseGameButton.setText("Pause");
            endGameButton.setEnabled(true);
            endTourButton.setEnabled(true);
            endRoundButton.setEnabled(true);
            if (timeRoundHasEnded) {
                startGameTimer();
            } else {
                startRoundTimer();
            }
        }
    }

    private void startRoundTimer() {
        roundCountDownTimer = new CountDownTimer(roundTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                roundTimeLeftInMillis = millisUntilFinished;
                updateRoundCountDownText();
            }

            @Override
            public void onFinish() {
                roundTimerTextView.setText("00:00");
                timeRoundHasEnded = true;
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

    private void pauseRoundTimer() {
        roundCountDownTimer.cancel();
    }

    private void startGameTimer() {
        gameCountDownTimer = new CountDownTimer(gameTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                gameTimeLeftInMillis = millisUntilFinished;

                updateGameCountDownText();
            }

            @Override
            public void onFinish() {
                updateGameCountDownText();
                new AlertDialog.Builder(GameActualPlayerActivity.this)
                        .setTitle("Exit")
                        .setMessage("Exit this view will provoke cancellation the game")
                        .setNegativeButton("End Round", (arg0, arg1) -> endRound())
                        .setPositiveButton("End Tour", (arg0, arg1) -> endTour())
                        .create().show();
            }
        }.start();
    }

    private void updateGameCountDownText() {
        int minutes = (int) (gameTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (gameTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        gameTimerTextView.setText(timeLeftFormatted);
    }

    private void pauseGameTimer() {
        gameCountDownTimer.cancel();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Exit this view will provoke cancellation the game")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, (arg0, arg1) -> endGame()).create().show();
    }

}
