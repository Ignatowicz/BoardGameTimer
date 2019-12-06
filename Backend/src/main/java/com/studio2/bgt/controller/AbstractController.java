package com.studio2.bgt.controller;

import com.studio2.bgt.model.entity.Player;
import com.studio2.bgt.model.enums.SendTo;
import com.studio2.bgt.model.helpers.NotificationHelper;
import com.studio2.bgt.model.helpers.PlayHelper;

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

    protected NotificationHelper prepareNotification(Player player, PlayHelper play, SendTo sendTo) {
        NotificationHelper startGameNotification = new NotificationHelper();
        Map<String, String> players = new HashMap<>();
        Set<String> topics = new HashSet<>();

        if (sendTo.equals(SendTo.FRIENDS)) {
            play.getFriends().forEach(f -> players.put(String.valueOf(f.getId()), f.getName()));
            play.getFriends().forEach(f -> topics.add("Player_" + f.getId()));
        } else if (sendTo.equals(SendTo.ACCEPTED)) {
            play.getAccepted().forEach(f -> players.put(String.valueOf(f.getId()), f.getName()));
            play.getAccepted().forEach(f -> topics.add("Player_" + f.getId()));
        } else if (sendTo.equals(SendTo.PLAYERS_TOUR_A)) {
            play.getPlayersTourA().forEach(f -> players.put(String.valueOf(f.getId()), f.getName()));
            play.getPlayersTourA().forEach(f -> topics.add("Player_" + f.getId()));
        } else if (sendTo.equals(SendTo.PLAYERS_TOUR_B)) {
            play.getPlayersTourB().forEach(f -> players.put(String.valueOf(f.getId()), f.getName()));
            play.getPlayersTourB().forEach(f -> topics.add("Player_" + f.getId()));
        } else if (sendTo.equals(SendTo.FIRST_PLAYER_TOUR_A)) {
            Player firstPlayerA = play.getPlayersTourA().peek();
            if (firstPlayerA != null) {
                players.put(String.valueOf(firstPlayerA.getId()), firstPlayerA.getName());
                topics.add("Player_" + firstPlayerA.getId());
            }
        } else if (sendTo.equals(SendTo.FIRST_PLAYER_TOUR_B)) {
            Player firstPlayerB = play.getPlayersTourB().peek();
            if (firstPlayerB != null) {
                players.put(String.valueOf(firstPlayerB.getId()), firstPlayerB.getName());
                topics.add("Player_" + firstPlayerB.getId());
            }
        } else if (sendTo.equals(SendTo.ALL_EXCEPT_FIRST_PLAYER_A)) {
            Player fpA = play.getPlayersTourA().remove();
            if (fpA != null) {
                play.getPlayersTourA().forEach(f -> players.put(String.valueOf(f.getId()), f.getName()));
                play.getPlayersTourA().forEach(f -> topics.add("Player_" + f.getId()));
                players.remove(String.valueOf(fpA.getId()), fpA.getName());
                topics.add("Player_" + fpA.getId());
            }
        } else if (sendTo.equals(SendTo.ALL_EXCEPT_FIRST_PLAYER_B)) {
            Player fpB = play.getPlayersTourB().remove();
            if (fpB != null) {
                play.getPlayersTourB().forEach(f -> players.put(String.valueOf(f.getId()), f.getName()));
                play.getPlayersTourB().forEach(f -> topics.add("Player_" + f.getId()));
                players.remove(String.valueOf(fpB.getId()), fpB.getName());
                topics.add("Player_" + fpB.getId());
            }
        }

        startGameNotification.setPlayer(player);
        startGameNotification.setPlay(play);
        startGameNotification.setPlayers(players);
        startGameNotification.setTopics(topics);

        return startGameNotification;
    }

}
