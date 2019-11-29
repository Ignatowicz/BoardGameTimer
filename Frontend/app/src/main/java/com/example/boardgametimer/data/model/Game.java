package com.example.boardgametimer.data.model;

import java.io.Serializable;

public class Game implements Serializable {
    private Long id;
    private String name;
    private int timeRound;
    private int timeGame;
    private int minPlayers;
    private int maxPlayers;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
