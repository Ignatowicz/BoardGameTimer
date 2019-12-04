package com.example.boardgametimer.utils;

import com.example.boardgametimer.data.model.LoggedInUser;

import java.util.HashSet;

public class ClearResponseFriends {

    public static LoggedInUser clearResponse(LoggedInUser user) {
        clearInfiniteFriendsLoop(user);
        return user;
    }

    public static LoggedInUser clearInfiniteFriendsLoop(LoggedInUser user) {
        user.getFriend1().forEach(f -> f.setFriend1(new HashSet<>()));
        user.getFriend1().forEach(f -> f.setFriend2(new HashSet<>()));
        user.getFriend2().forEach(f -> f.setFriend1(new HashSet<>()));
        user.getFriend2().forEach(f -> f.setFriend2(new HashSet<>()));
        return user;
    }

}
