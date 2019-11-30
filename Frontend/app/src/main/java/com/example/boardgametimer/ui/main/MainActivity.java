package com.example.boardgametimer.ui.main;

import android.content.Intent;
import android.os.Bundle;

import com.example.boardgametimer.data.model.Game;
import com.example.boardgametimer.data.model.LoggedInUser;
import com.example.boardgametimer.ui.friends.FriendsActivity;
import com.example.boardgametimer.ui.newgame.NewGameActivity;
import com.example.boardgametimer.ui.settings.SettingsActivity;
import com.example.boardgametimer.ui.startgame.StartgameActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.boardgametimer.R;
import com.example.boardgametimer.ui.newgame.NewGameActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Adapter.ItemClickListener {
    Adapter adapter;
    LoggedInUser user;
    ArrayList<Game> animalNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);

        // TODO: after createPlayer, get the playerId and subscribe to topic="Player_" + String.valueOf(playerId)
        String TOPIC = "Player_1";
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC);

        // tokens -> maybe there is no need to have it cause of topic usage instead of tokens (it easier? to have it done)
//        // TODO??: onCreateAccount
//        FirebaseInstanceId.getInstance().getInstanceId()
//                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                        if (!task.isSuccessful()) {
//                            Log.w(TAG, "getInstanceId failed", task.getException());
//                            return;
//                        }
//
//                        // Get new Instance ID token
//                        String token = task.getResult().getToken();
//
//                        // Log and toast
//                        String msg = getString(R.string.msg_token_fmt, token);
//                        Log.d(TAG, msg);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
//                    }
//                });

        this.user = (LoggedInUser) getIntent().getSerializableExtra("user");


        if (user.getGames() != null) {
            animalNames.addAll(user.getGames());
        }

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(this, animalNames);
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
                intent.putExtra("user",user);
                startActivity(intent);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                intent.putExtra("user",user);
                startActivity(intent);
            }
        });

        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FriendsActivity.class);
                intent.putExtra("user",user);
                startActivity(intent);
            }
        });
    }
        @Override
        public void onItemClick(View view, int position) {
            Intent intent = new Intent(MainActivity.this, StartgameActivity.class);
            intent.putExtra("user", user);
            intent.putExtra("position", position);
            intent.putExtra("gamesList", animalNames);

            startActivity(intent);
        }
}
