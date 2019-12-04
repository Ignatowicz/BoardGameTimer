package com.example.boardgametimer.ui.startgame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boardgametimer.R;
import com.example.boardgametimer.api.HttpUtils;
import com.example.boardgametimer.data.model.Game;
import com.example.boardgametimer.data.model.LoggedInUser;
import com.example.boardgametimer.data.model.PlayHelper;
import com.example.boardgametimer.data.model.StartGameHelper;
import com.example.boardgametimer.ui.game.GameActivity;
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

public class StartgameActivity extends AppCompatActivity implements AdapterPlayers.ItemClickListener, AdapterFriends.ItemClickListener {

    AdapterPlayers adapterPlayers;
    AdapterFriends adapterFriends;
    LoggedInUser user;
    List<Game> games = new ArrayList<>();
    Set<LoggedInUser> players = new HashSet<>();
    Set<LoggedInUser> friends = new HashSet<>();
    TextView playersNumberTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startgame);

        // hide return home button
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);

        TextView nameTextView = findViewById(R.id.gameNameTextView);
        playersNumberTextView = findViewById(R.id.playersNumberTextView);
        TextView roundTimeTextView = findViewById(R.id.roundTimeTextView);
        TextView gameTimeTextView = findViewById(R.id.gameTimeTextView);
        Button startGameButton = findViewById(R.id.startGameButton);

        this.user = (LoggedInUser) getIntent().getSerializableExtra("user");
        this.games = (ArrayList<Game>) getIntent().getSerializableExtra("games");
        int position = getIntent().getIntExtra("position", 0);

        friends.addAll(user.getFriend1());
        friends.addAll(user.getFriend2());

        updateView();

        Game currentGame = games.get(position);

        nameTextView.setText(currentGame.getName());
        roundTimeTextView.setText(Integer.toString(currentGame.getTimeRound()));
        gameTimeTextView.setText(Integer.toString(currentGame.getTimeGame()));

        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play(currentGame);
            }
        });
    }

    private void play(Game currentGame) {
        HttpUtils.get("play/" + currentGame.getId(), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Gson gson = new Gson();
                JsonElement element = gson.fromJson(response.toString(), JsonElement.class);
                PlayHelper play = gson.fromJson(element, PlayHelper.class);
                startGame(play, currentGame);
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

    private void startGame(PlayHelper play, Game currentGame) {
        StartGameHelper startGameHelper = new StartGameHelper();
        startGameHelper.setPlayId(play.getPlayId());
        players.forEach(p -> startGameHelper.getPlayersId().add(p.getId()));

        Gson gson = new Gson();
        String jsonParams = gson.toJson(startGameHelper);
        final StringEntity[] entity;
        try {
            entity = new StringEntity[]{new StringEntity(jsonParams)};
            HttpUtils.post(getApplicationContext(), "play/startGame", entity[0], "application/json", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    System.out.println(statusCode + response.toString());
                    Intent intent = new Intent(StartgameActivity.this, GameActivity.class);
                    intent.putExtra("user", user);
                    intent.putExtra("game", currentGame);
                    intent.putExtra("play", play);
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

    private void updatePlayersRecyclerView() {
        // set up the PlayersRecyclerView
        RecyclerView recyclerView = findViewById(R.id.startgame_players_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterPlayers = new AdapterPlayers(this, players);
        adapterPlayers.setClickListener(this);
        recyclerView.setAdapter(adapterPlayers);
    }

    private void updateFriendsRecyclerView() {
        // set up the FriendsRecyclerView
        RecyclerView recyclerView = findViewById(R.id.startgame_friends_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterFriends = new AdapterFriends(this, friends);
        adapterFriends.setClickListener(this);
        recyclerView.setAdapter(adapterFriends);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("user", user);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onItemFriendClick(View view, int position) {
        players.add(adapterFriends.getItem(position));
        friends.remove(adapterFriends.getItem(position));
        updateView();
    }

    @Override
    public void onItemPlayerClick(View view, int position) {
        friends.add(adapterPlayers.getItem(position));
        players.remove(adapterPlayers.getItem(position));
        updateView();
    }

    private void updateView() {
        playersNumberTextView.setText(Integer.toString(players.size()));
        updatePlayersRecyclerView();
        updateFriendsRecyclerView();
    }

}
