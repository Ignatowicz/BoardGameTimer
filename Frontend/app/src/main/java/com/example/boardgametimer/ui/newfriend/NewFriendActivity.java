package com.example.boardgametimer.ui.newfriend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boardgametimer.R;
import com.example.boardgametimer.api.HttpUtils;
import com.example.boardgametimer.data.model.LoggedInUser;
import com.example.boardgametimer.ui.main.MainActivity;
import com.example.boardgametimer.utils.ClearResponseFriends;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class NewFriendActivity extends AppCompatActivity implements Adapter.ItemClickListener {

    Adapter adapter;
    LoggedInUser user;
    Set<LoggedInUser> friends = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend);

        // hide return home button
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);

        this.user = (LoggedInUser) getIntent().getSerializableExtra("user");

        getPlayers();
    }

    private void getPlayers() {
        HttpUtils.get("players", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                System.out.println(statusCode + response.toString());

                Gson gson = new Gson();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = response.getJSONObject(i);
                        JsonElement element = gson.fromJson(object.toString(), JsonElement.class);
                        LoggedInUser foundPlayer = gson.fromJson(element, LoggedInUser.class);

                        // do not display actual player and his actual friends
                        if (user.getId().equals(foundPlayer.getId())) {
                            continue;
                        }

                        boolean flag = false;
                        for (LoggedInUser friend : user.getFriend1()) {
                            if (friend.getId().equals(foundPlayer.getId())) {
                                flag = true;
                            }
                        }

                        for (LoggedInUser friend : user.getFriend2()) {
                            if (friend.getId().equals(foundPlayer.getId())) {
                                flag = true;
                            }
                        }

                        if (flag) {
                            continue;
                        }

                        // do not display actual player and his actual friends
                        friends.add(foundPlayer);

                        updateRecyclerView();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
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

    private void updateRecyclerView() {

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.new_friends_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(this, friends);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        addFriend(position);
    }

    public void addFriend(int position) {
        NewFriendActivity.this.user.getFriend1().add(adapter.getItem(position));
        ClearResponseFriends.clearResponse(user);

        Gson gson = new Gson();
        String jsonParams = gson.toJson(user);
        final StringEntity[] entity;
        try {
            entity = new StringEntity[]{new StringEntity(jsonParams)};
            HttpUtils.put(getApplicationContext(), "players/" + user.getId(), entity[0], "application/json", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    System.out.println(statusCode + response.toString());

                    Gson gson = new Gson();
                    JsonElement element = gson.fromJson(response.toString(), JsonElement.class);
                    LoggedInUser addedUser = gson.fromJson(element, LoggedInUser.class);

                    Intent intent = new Intent(NewFriendActivity.this, MainActivity.class);
                    intent.putExtra("user", addedUser);
                    setResult(RESULT_OK, intent);
                    finish();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    NewFriendActivity.this.user.getFriend1().remove(adapter.getItem(position));
                    System.out.println(statusCode + responseString);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    NewFriendActivity.this.user.getFriend1().remove(adapter.getItem(position));
                    System.out.println(errorResponse.toString());
                }
            });

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("user", user);
        setResult(RESULT_OK, intent);
        finish();
    }

}
