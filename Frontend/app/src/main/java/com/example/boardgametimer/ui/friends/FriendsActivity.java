package com.example.boardgametimer.ui.friends;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boardgametimer.R;
import com.example.boardgametimer.api.HttpUtils;
import com.example.boardgametimer.data.model.LoggedInUser;
import com.example.boardgametimer.ui.newfriend.NewFriendActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class FriendsActivity extends AppCompatActivity implements Adapter.ItemClickListener {

    Adapter adapter;
    LoggedInUser user;
    Set<LoggedInUser> friends = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        // hide return home button
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);

        this.user = (LoggedInUser) getIntent().getSerializableExtra("user");

        if (user.getFriend1() != null) {
            friends.addAll(user.getFriend1());
        }
        if (user.getFriend2() != null) {
            friends.addAll(user.getFriend2());
        }

        updateRecyclerView();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(FriendsActivity.this, NewFriendActivity.class);
            intent.putExtra("user", user);
            startActivityForResult(intent, 1);
        });
    }

    // unfriend selected player
    @Override
    public void onItemClick(View view, int position) {
        unfriend(position);
    }

    private void unfriend(int position) {
        if (FriendsActivity.this.user.getFriend1().contains(adapter.getItem(position))) {
            FriendsActivity.this.user.getFriend1().remove(adapter.getItem(position));
        }
        if (FriendsActivity.this.user.getFriend2().contains(adapter.getItem(position))) {
            FriendsActivity.this.user.getFriend2().remove(adapter.getItem(position));
        }

        Gson gson = new Gson();
        String jsonParams = gson.toJson(user);
        final StringEntity[] entity;
        try {
            entity = new StringEntity[]{new StringEntity(jsonParams)};
            HttpUtils.put(getApplicationContext(), "players/" + user.getId(), entity[0], "application/json", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    System.out.println(statusCode + response.toString());

                    friends.remove(adapter.getItem(position));
                    updateRecyclerView();
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


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                this.user = (LoggedInUser) data.getSerializableExtra("user");
                friends.addAll(user.getFriend1());
                friends.addAll(user.getFriend2());
                updateRecyclerView();
            }
        }
    }

    private void updateRecyclerView() {

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.friends_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(this, friends);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("user", user);
        setResult(RESULT_OK, intent);
        finish();
    }

}
