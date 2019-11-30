package com.example.boardgametimer.ui.register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.boardgametimer.R;
import com.example.boardgametimer.api.HttpUtils;
import com.example.boardgametimer.data.model.LoggedInUser;
import com.example.boardgametimer.ui.main.MainActivity;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText nameEditText = findViewById(R.id.nameEditText);
        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        EditText repeatPasswordEditText = findViewById(R.id.repeatPasswordEditText);
        Button registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                LoggedInUser user = new LoggedInUser(name, email, password);
                createUser(user);
            }
        });
    }

    void createUser(LoggedInUser user){
        Gson gson = new Gson();
        String jsonParams = gson.toJson(user);
        final StringEntity[] entity;
        try {
            entity = new StringEntity[]{new StringEntity(jsonParams)};
            HttpUtils.post(getApplicationContext(), "players/add", entity[0], "application/json", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    JsonElement element = gson.fromJson(response.toString(), JsonElement.class);
                    LoggedInUser addedUser = gson.fromJson(element, LoggedInUser.class);

                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.putExtra("user", addedUser);
                    startActivity(intent);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    System.out.println(statusCode + responseString);
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
}

