package com.example.boardgametimer.ui.friends;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boardgametimer.R;
import com.example.boardgametimer.data.model.LoggedInUser;
import com.example.boardgametimer.ui.newfriend.NewFriendActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashSet;
import java.util.Set;

public class FriendsActivity extends AppCompatActivity implements Adapter.ItemClickListener {

    Adapter adapter;
    LoggedInUser user;
    Set<LoggedInUser> friends = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        this.user = (LoggedInUser) getIntent().getSerializableExtra("user");


        if (user.getFriend1() != null) {
            friends.addAll(user.getFriend1());
        }
        if (user.getFriend2() != null) {
            friends.addAll(user.getFriend2());
        }

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.friends_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(this, friends);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FriendsActivity.this, NewFriendActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
    }

    // unfriend selected player
    // TODO: call unfriend method
    @Override
    public void onItemClick(View view, int position) {
        unfriend(position);
    }

    private void unfriend(int position) {
        System.out.println("Unfriending: " + adapter.getItem(position));
        // TODO
    }

}
