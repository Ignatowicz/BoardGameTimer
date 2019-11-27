package com.studio2.bgt.controller;

import com.studio2.bgt.model.entity.Player;
import com.studio2.bgt.model.enums.SendType;
import com.studio2.bgt.model.helpers.PlayHelper;
import com.studio2.bgt.model.helpers.StartGameNotificationHelper;

import java.util.*;

public abstract class AbstractController {

    protected PlayHelper clearResponse(PlayHelper play) {
        if (!play.getFriends().isEmpty()) {
            clearResponse(play.getFriends());
        }

        if (!play.getAccepted().isEmpty()) {
            clearResponse(play.getAccepted());
        }

        if (!play.getPlayersTourA().isEmpty()) {
            clearResponse(play.getPlayersTourA());
        }

        if (!play.getPlayersTourB().isEmpty()) {
            clearResponse(play.getPlayersTourB());
        }

        return play;
    }

    protected Queue<Player> clearResponse(Queue<Player> players) {
        players.forEach(this::clearResponse);
        return players;
    }

    protected Set<Player> clearResponse(Set<Player> players) {
        players.forEach(this::clearResponse);
        return players;
    }

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

    protected StartGameNotificationHelper preparePlayersToWhomNotificationWillBeSent(PlayHelper play, SendType sendType) {
        StartGameNotificationHelper startGameNotification = new StartGameNotificationHelper();
        Map<String, String> players = new HashMap<>();
        Set<String> topics = new HashSet<>();

        switch (sendType) {
            case FRIENDS:
                play.getFriends().forEach(f -> players.put(String.valueOf(f.getId()), f.getName()));
                play.getFriends().forEach(f -> topics.add("Player_" + f.getId()));
            case ACCEPTED:
                play.getAccepted().forEach(f -> players.put(String.valueOf(f.getId()), f.getName()));
                play.getAccepted().forEach(f -> topics.add("Player_" + f.getId()));
            case PLAYERS_TOUR_A:
                play.getPlayersTourA().forEach(f -> players.put(String.valueOf(f.getId()), f.getName()));
                play.getPlayersTourA().forEach(f -> topics.add("Player_" + f.getId()));
            case PLAYERS_TOUR_B:
                play.getPlayersTourB().forEach(f -> players.put(String.valueOf(f.getId()), f.getName()));
                play.getPlayersTourB().forEach(f -> topics.add("Player_" + f.getId()));
        }

        startGameNotification.setPlayers(players);
        startGameNotification.setTopics(topics);

        return startGameNotification;
    }

}
