package com.example.boardgametimer.ui.main;

import android.content.Intent;
import android.os.Bundle;

import com.example.boardgametimer.data.model.Game;
import com.example.boardgametimer.data.model.LoggedInUser;
import com.example.boardgametimer.ui.friends.FriendsActivity;
import com.example.boardgametimer.ui.newgame.NewGameActivity;
import com.example.boardgametimer.ui.settings.SettingsActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.boardgametimer.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MyAdapter.ItemClickListener {
    MyAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);

        LoggedInUser user = (LoggedInUser) getIntent().getSerializableExtra("user");
        System.out.println(user);

        ArrayList<Game> animalNames = new ArrayList<>();
        animalNames.addAll(user.getGames());

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter(this, animalNames);
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
                startActivity(intent);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FriendsActivity.class);
                startActivity(intent);
            }
        });
    }
        @Override
        public void onItemClick(View view, int position) {
            Intent intent = new Intent(MainActivity.this, NewGameActivity.class);
            startActivity(intent);
        }
}
