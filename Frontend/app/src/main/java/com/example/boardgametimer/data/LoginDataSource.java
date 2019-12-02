package com.example.boardgametimer.data;

import android.content.Context;

import com.example.boardgametimer.api.HttpUtils;
import com.example.boardgametimer.data.model.LoggedInUser;
import com.example.boardgametimer.ui.login.LoginViewModel;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(Context context, String username, String password, LoginViewModel callback) {
        try {
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("email", username);
            jsonParams.put("password", password);
            final StringEntity[] entity = {new StringEntity(jsonParams.toString())};


            HttpUtils.post(context, "players/login", entity[0], "application/json", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Gson gson = new Gson();
                    JsonElement element = gson.fromJson(response.toString(), JsonElement.class);
                    LoggedInUser user = gson.fromJson(element, LoggedInUser.class);
                    callback.callback(new Result.Success<>(user));
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    callback.callback(new Result.Error(new IOException(statusCode + responseString)));
                }
            });


        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new Result.Error(new IOException("Error logging in"));
    }

    public void logout() {
        // TODO: revoke authentication
    }

}
