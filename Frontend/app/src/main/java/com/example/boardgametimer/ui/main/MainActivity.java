package com.example.boardgametimer.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boardgametimer.R;
import com.example.boardgametimer.data.model.Game;
import com.example.boardgametimer.data.model.LoggedInUser;
import com.example.boardgametimer.ui.friends.FriendsActivity;
import com.example.boardgametimer.ui.newgame.NewGameActivity;
import com.example.boardgametimer.ui.settings.SettingsActivity;
import com.example.boardgametimer.ui.startgame.StartgameActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Adapter.ItemClickListener {

    Adapter adapter;
    LoggedInUser user;
    ArrayList<Game> games = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = findViewById(R.id.toolbar);


        this.user = (LoggedInUser) getIntent().getSerializableExtra("user");


        if (user.getGames() != null) {
            games.addAll(user.getGames());
        }

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.main_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(this, games);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);


        final Button settingsButton = findViewById(R.id.settings_button);
        final Button friendsButton = findViewById(R.id.friends_button);

        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewGameActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FriendsActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(MainActivity.this, StartgameActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("position", position);
        intent.putExtra("gamesList", games);

        startActivity(intent);
    }

}
