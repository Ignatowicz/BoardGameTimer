package com.example.boardgametimer.data.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser implements Serializable {
    private int id;

    private String name;
    private String email;
    private String password;
    private Set<Game> games;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Game> getGames() {
        return games;
    }

    public void setGames(Set<Game> games) {
        this.games = games;
    }

    public void addGame(Game game){ this.games.add(game);}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private Set<LoggedInUser> friend1;
    private Set<LoggedInUser> friend2;
    public LoggedInUser(int id, String name, String email, String password, Set<Game> games, Set<LoggedInUser> friend1, Set<LoggedInUser> friend2) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.games = games;
        this.friend1 = friend1;
        this.friend2 = friend2;
    }
    public LoggedInUser(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.games = new HashSet<>();

    }


    public String getDisplayName() {
        return name;
    }

    @Override
    public String toString() {
        return "LoggedInUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", games=" + games +
                ", friend1=" + friend1 +
                ", friend2=" + friend2 +
                '}';
    }
}
