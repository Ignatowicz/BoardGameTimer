package com.example.boardgametimer.data.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class StartGameHelper implements Serializable {

    private Long playId;
    private Set<Long> playersId = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StartGameHelper that = (StartGameHelper) o;
        return playId.equals(that.playId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playId);
    }

    public StartGameHelper() {
    }

    public StartGameHelper(Long playId, Set<Long> playersId) {
        this.playId = playId;
        this.playersId = playersId;
    }

    public Long getPlayId() {
        return playId;
    }

    public void setPlayId(Long playId) {
        this.playId = playId;
    }

    public Set<Long> getPlayersId() {
        return playersId;
    }

    public void setPlayersId(Set<Long> playersId) {
        this.playersId = playersId;
    }

    @Override
    public String toString() {
        return "StartGameHelper{" +
                "playId=" + playId +
                ", playersId=" + playersId +
                '}';
    }

}