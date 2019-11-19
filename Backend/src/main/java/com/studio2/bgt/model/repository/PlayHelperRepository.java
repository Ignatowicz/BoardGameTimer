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
public class PlayHelperRepository {

    @Bean
    public Set<PlayHelper> playHelperSession() {
        return new HashSet<>();
    }

    @Resource(name = "playHelperSession")
    public Set<PlayHelper> playHelperRepository;

    public Set<PlayHelper> getAllPlays() {
        return playHelperRepository;
    }

    public PlayHelper findPlayById(Long id) {
        List<PlayHelper> foundPlay = playHelperRepository.stream().filter(p -> p.getPlayId().equals(id)).collect(Collectors.toList());
        return foundPlay.get(0);
    }

    public void create(PlayHelper play) {
        playHelperRepository.add(play);
    }

    public void update(PlayHelper updatedPlay) {
        PlayHelper play = PlayHelper.builder()
                .playId(updatedPlay.getPlayId())
                .gameId(updatedPlay.getGameId())
                .playerId(updatedPlay.getPlayerId())
                .gameName(updatedPlay.getGameName())
                .timeRound(updatedPlay.getTimeRound())
                .timeGame(updatedPlay.getTimeGame())
                .minPlayers(updatedPlay.getMinPlayers())
                .maxPlayers(updatedPlay.getMaxPlayers())
                .friends(updatedPlay.getFriends())
                .build();
        playHelperRepository.remove(findPlayById(updatedPlay.getPlayId()));
        playHelperRepository.add(play);
    }

    public void delete(Long id) {
        playHelperRepository.remove(findPlayById(id));
    }
}
