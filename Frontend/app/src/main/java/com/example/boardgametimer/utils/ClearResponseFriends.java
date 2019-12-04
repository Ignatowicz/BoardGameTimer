package com.example.boardgametimer.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.boardgametimer.data.model.LoggedInUser;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class ClearResponseFriends {

//    public static LoggedInUser clearResponse(LoggedInUser user) {
//        if (!user.getFriends().isEmpty()) {
//            clearResponse(user.getFriend1());
//        }

//        if (!user.getAccepted().isEmpty()) {
//            clearResponse(user.getAccepted());
//        }
//
//        if (!user.getLoggedInUsersTourA().isEmpty()) {
//            clearResponse(user.getLoggedInUsersTourA());
//        }
//
//        if (!user.getLoggedInUsersTourB().isEmpty()) {
//            clearResponse(user.getLoggedInUsersTourB());
//        }
//
//        return user;
//    }
//
//    public static Queue<LoggedInUser> clearResponse(Queue<LoggedInUser> users) {
//        users.forEach(this::clearResponse);
//        return users;
//    }
//
//    public static Set<LoggedInUser> clearResponse(Set<LoggedInUser> users) {
//        users.forEach(this::clearResponse);
//        return users;
//    }
//

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
