package com.studio2.bgt.model.repository;

import com.studio2.bgt.model.helpers.PlayHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class PlayRepository {

    @Bean
    public Long playId() {
        return Long.parseLong("0");
    }

    @Resource(name = "playId")
    public Long playId = 0L;

    @Bean
    public Set<PlayHelper> playSession() {
        return new HashSet<>();
    }

    @Resource(name = "playSession")
    public Set<PlayHelper> playRepository;

    public Set<PlayHelper> findAllPlays() {
        return playRepository;
    }

    public PlayHelper findPlayById(Long id) {
        List<PlayHelper> foundPlay = playRepository.stream().filter(p -> p.getPlayId().equals(id)).collect(Collectors.toList());
        return foundPlay.get(0);
    }

    public void createPlay(PlayHelper play) {
        playRepository.add(play);
    }

    public void updatePlay(PlayHelper updatedPlay) {
        PlayHelper play = PlayHelper.builder()
                .playId(updatedPlay.getPlayId())
                .gameId(updatedPlay.getGameId())
                .playerId(updatedPlay.getPlayerId())
                .isTourA(updatedPlay.isTourA())
                .playerGameStarterId(updatedPlay.getPlayerGameStarterId())
                .gameName(updatedPlay.getGameName())
                .timeRound(updatedPlay.getTimeRound())
                .timeGame(updatedPlay.getTimeGame())
                .minPlayers(updatedPlay.getMinPlayers())
                .maxPlayers(updatedPlay.getMaxPlayers())
                .friends(updatedPlay.getFriends())
                .accepted(updatedPlay.getAccepted())
                .playersTourA(updatedPlay.getPlayersTourA())
                .playersTourB(updatedPlay.getPlayersTourB())
                .roundTimePlayers(updatedPlay.getRoundTimePlayers())
                .gameTimePlayers(updatedPlay.getGameTimePlayers())
                .build();
        playRepository.remove(findPlayById(updatedPlay.getPlayId()));
        playRepository.add(play);
    }

    public void deletePlay(Long id) {
        playRepository.remove(findPlayById(id));
    }

}
