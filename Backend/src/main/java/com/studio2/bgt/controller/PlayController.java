package com.studio2.bgt.controller;

import com.studio2.bgt.model.entity.Game;
import com.studio2.bgt.model.entity.Player;
import com.studio2.bgt.model.helpers.PlayHelper;
import com.studio2.bgt.model.repository.GameRepository;
import com.studio2.bgt.model.repository.PlayHelperRepository;
import com.studio2.bgt.model.repository.PlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/play")
@CrossOrigin
public class PlayController {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    PlayHelperRepository playHelperRepository;

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
                    .gameId(gameId)
                    .playerId(game.getId())
                    .gameName(game.getName())
                    .timeRound(game.getTimeRound())
                    .timeGame(game.getTimeGame())
                    .minPlayers(game.getMinPlayers())
                    .maxPlayers(game.getMaxPlayers())
                    .friends(friends)
                    .build();

            playHelperRepository.save(play);
            return ResponseEntity.ok().body(play);
        }
        return ResponseEntity.notFound().eTag("GAME NOT FOUND").build();
    }

    @GetMapping("/startGame/{id}")
    public ResponseEntity<?> startGame(@PathVariable Long id) {
        Optional<PlayHelper> playHelperOptional = playHelperRepository.findById(id);
        if (playHelperOptional.isPresent()) {
            PlayHelper play = playHelperOptional.get();

            Set<Player> friends = new HashSet<>();
            play.getFriends().forEach(f -> friends.add(playerRepository.findPlayerById(f)));

//            friends.forEach();  // TODO: sent requests to all friends

            return ResponseEntity.ok().body("Start game requests sent to your friends!");
        }
        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    }

//    @GetMapping
//    public Iterable findAll() {
//        return gameRepository.findAll();
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<?> findById(@PathVariable Long id) {
//        Optional<Game> game = gameRepository.findById(id);
//        if (game.isPresent()) {
//            return ResponseEntity.ok().body(game);
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }
//
//    @PostMapping("/add")
//    @ResponseStatus(HttpStatus.CREATED)
//    public ResponseEntity<Game> createGame(@Valid @RequestBody Game game) throws URISyntaxException {
//        Game result = gameRepository.save(game);
//        return ResponseEntity.created(new URI("/api/games/" + result.getId()))
//                .body(result);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<Game> updateGame(@PathVariable Long id, @Valid @RequestBody Game updatedGame) {
//        Optional<Game> gameOptional = gameRepository.findById(id);
//        if (gameOptional.isPresent()) {
//            Game game = gameOptional.get();
//            game.setName(updatedGame.getName());
//            game.setTimeRound(updatedGame.getTimeRound());
//            game.setTimeGame(updatedGame.getTimeGame());
//            game.setMinPlayers(updatedGame.getMinPlayers());
//            game.setMaxPlayers(updatedGame.getMaxPlayers());
//            game.setPlayer(updatedGame.getPlayer());
//            gameRepository.save(game);
//            return ResponseEntity.ok().body(game);
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> deleteGame(@PathVariable Long id) {
//        gameRepository.deleteById(id);
//        return ResponseEntity.ok().build();
//    }
}
