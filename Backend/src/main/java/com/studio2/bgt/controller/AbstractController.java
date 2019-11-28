package com.studio2.bgt.controller;

import com.studio2.bgt.model.entity.Player;

import java.util.HashSet;

public abstract class AbstractController {

    protected Player clearResponse(Player player) {
        clearInfiniteFriendsLoop(player);
        return player;
    }

    protected Player clearInfiniteFriendsLoop(Player player) {
        player.getFriend1().forEach(f -> f.setFriend1(new HashSet<>()));
        player.getFriend1().forEach(f -> f.setFriend2(new HashSet<>()));
        player.getFriend2().forEach(f -> f.setFriend1(new HashSet<>()));
        player.getFriend2().forEach(f -> f.setFriend2(new HashSet<>()));
        return player;
    }

}
