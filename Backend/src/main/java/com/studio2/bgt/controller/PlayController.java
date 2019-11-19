package com.studio2.bgt.controller;

import com.studio2.bgt.model.entity.Game;
import com.studio2.bgt.model.entity.Player;
import com.studio2.bgt.model.helpers.PlayHelper;
import com.studio2.bgt.model.repository.GameRepository;
import com.studio2.bgt.model.repository.PlayHelperRepository;
import com.studio2.bgt.model.repository.PlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/play")
@CrossOrigin
public class PlayController {

    private Long playId = 0L;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository gameRepository;

    @Resource(name = "playHelperRepository")
    private PlayHelperRepository playHelperRepository;

    private Long getNextPlayId() {
        return ++playId;
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<?> play(@PathVariable Long gameId) {
        Optional<Game> gameOptional = gameRepository.findById(gameId);
        if (gameOptional.isPresent()) {
            Game game = gameOptional.get();

            Set<Long> friends = new HashSet<>();
            Player player = game.getPlayer();
            player.getFriend1().forEach(f -> friends.add(f.getId()));
            player.getFriend2().forEach(f -> friends.add(f.getId()));

            PlayHelper play = PlayHelper.builder()
                    .playId(getNextPlayId())
                    .gameId(gameId)
                    .playerId(game.getId())
                    .gameName(game.getName())
                    .timeRound(game.getTimeRound())
                    .timeGame(game.getTimeGame())
                    .minPlayers(game.getMinPlayers())
                    .maxPlayers(game.getMaxPlayers())
                    .friends(friends)
                    .build();

            playHelperRepository.create(play);
            return ResponseEntity.ok().body(play);
        }
        return ResponseEntity.notFound().eTag("GAME NOT FOUND").build();
    }

    @GetMapping("/startGame/{id}")
    public ResponseEntity<?> startGame(@PathVariable Long id) {
        PlayHelper play = playHelperRepository.findPlayById(id);

        Set<Player> friends = new HashSet<>();
        play.getFriends().forEach(f -> friends.add(playerRepository.findPlayerById(f)));

//        friends.forEach();  // TODO: sent requests to all friends


        return ResponseEntity.ok().body("Start game requests sent to your friends!");
    }
}
