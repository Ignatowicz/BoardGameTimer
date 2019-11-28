package com.example.boardgametimer.data;

import android.content.Context;

import com.example.boardgametimer.api.HttpUtils;
import com.example.boardgametimer.data.model.LoggedInUser;
import com.loopj.android.http.*;
import org.json.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(Context context, String username, String password) {

        try {
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("email", "jan.kowalski@gmail.com");
            jsonParams.put("password", "admin");
            StringEntity entity = new StringEntity(jsonParams.toString());

            HttpUtils.post(context, "players/login", entity,"application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println("w1");
//                LoggedInUser fakeUser =
//                        new LoggedInUser(
//                                java.util.UUID.randomUUID().toString(),
//                                "Jane Doe");
//                return new Result.Success<>(fakeUser);

            }
        });

        } catch (JSONException| UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new Result.Error(new IOException("Error logging in"));
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
