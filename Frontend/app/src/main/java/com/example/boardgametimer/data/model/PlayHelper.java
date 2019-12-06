package com.example.boardgametimer.data.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

public class PlayHelper implements Serializable {

    private Long playId;
    private Long gameId;
    private Long playerId;
    private Long playerGameStarterId;
    private boolean isTourA = true;
    private String gameName;
    private int timeRound;
    private int timeGame;
    private int minPlayers;
    private int maxPlayers;
    private Set<LoggedInUser> friends = new HashSet<>();
    private Set<LoggedInUser> accepted = new HashSet<>();
    private Queue<LoggedInUser> playersTourA = new LinkedList<>();
    private Queue<LoggedInUser> playersTourB = new LinkedList<>();
    private Map<Long, Long> roundTimePlayers = new HashMap<>();
    private Map<Long, Long> gameTimePlayers = new HashMap<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayHelper that = (PlayHelper) o;
        return playId.equals(that.playId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playId);
    }

    public PlayHelper() {
    }

    public PlayHelper(Long playId, Long gameId, Long playerId, Long playerGameStarterId, boolean isTourA, String gameName, int timeRound, int timeGame, int minPlayers, int maxPlayers, Set<LoggedInUser> friends, Set<LoggedInUser> accepted, Queue<LoggedInUser> playersTourA, Queue<LoggedInUser> playersTourB, Map<Long, Long> roundTimePlayers, Map<Long, Long> gameTimePlayers) {
        this.playId = playId;
        this.gameId = gameId;
        this.playerId = playerId;
        this.playerGameStarterId = playerGameStarterId;
        this.isTourA = isTourA;
        this.gameName = gameName;
        this.timeRound = timeRound;
        this.timeGame = timeGame;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.friends = friends;
        this.accepted = accepted;
        this.playersTourA = playersTourA;
        this.playersTourB = playersTourB;
        this.roundTimePlayers = roundTimePlayers;
        this.gameTimePlayers = gameTimePlayers;
    }

    public Long getPlayId() {
        return playId;
    }

    public void setPlayId(Long playId) {
        this.playId = playId;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public Long getPlayerGameStarterId() {
        return playerGameStarterId;
    }

    public void setPlayerGameStarterId(Long playerGameStarterId) {
        this.playerGameStarterId = playerGameStarterId;
    }

    public boolean isTourA() {
        return isTourA;
    }

    public void setTourA(boolean tourA) {
        isTourA = tourA;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public int getTimeRound() {
        return timeRound;
    }

    public void setTimeRound(int timeRound) {
        this.timeRound = timeRound;
    }

    public int getTimeGame() {
        return timeGame;
    }

    public void setTimeGame(int timeGame) {
        this.timeGame = timeGame;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public Set<LoggedInUser> getFriends() {
        return friends;
    }

    public void setFriends(Set<LoggedInUser> friends) {
        this.friends = friends;
    }

    public Set<LoggedInUser> getAccepted() {
        return accepted;
    }

    public void setAccepted(Set<LoggedInUser> accepted) {
        this.accepted = accepted;
    }

    public Queue<LoggedInUser> getPlayersTourA() {
        return playersTourA;
    }

    public void setPlayersTourA(Queue<LoggedInUser> playersTourA) {
        this.playersTourA = playersTourA;
    }

    public Queue<LoggedInUser> getPlayersTourB() {
        return playersTourB;
    }

    public void setPlayersTourB(Queue<LoggedInUser> playersTourB) {
        this.playersTourB = playersTourB;
    }

    public Map<Long, Long> getRoundTimePlayers() {
        return roundTimePlayers;
    }

    public void setRoundTimePlayers(Map<Long, Long> roundTimePlayers) {
        this.roundTimePlayers = roundTimePlayers;
    }

    public Map<Long, Long> getGameTimePlayers() {
        return gameTimePlayers;
    }

    public void setGameTimePlayers(Map<Long, Long> gameTimePlayers) {
        this.gameTimePlayers = gameTimePlayers;
    }

    @Override
    public String toString() {
        return "PlayHelper{" +
                "playId=" + playId +
                ", gameId=" + gameId +
                ", playerId=" + playerId +
                ", playerGameStarterId=" + playerGameStarterId +
                ", isTourA=" + isTourA +
                ", gameName='" + gameName + '\'' +
                ", timeRound=" + timeRound +
                ", timeGame=" + timeGame +
                ", minPlayers=" + minPlayers +
                ", maxPlayers=" + maxPlayers +
                ", friends=" + friends +
                ", accepted=" + accepted +
                ", playersTourA=" + playersTourA +
                ", playersTourB=" + playersTourB +
                ", roundTimePlayers=" + roundTimePlayers +
                ", gameTimePlayers=" + gameTimePlayers +
                '}';
    }

}
