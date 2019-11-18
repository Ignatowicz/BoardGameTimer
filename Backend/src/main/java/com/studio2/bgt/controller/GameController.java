package com.studio2.bgt.controller;

import com.studio2.bgt.model.entity.Game;
import com.studio2.bgt.model.repository.GameRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/games")
@CrossOrigin
public class GameController {

    @Autowired
    private GameRepository gameRepository;

    @GetMapping
    public Iterable findAll() {
        return gameRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        Optional<Game> game = gameRepository.findById(id);
        if (game.isPresent()) {
            return ResponseEntity.ok().body(game);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Game> createGame(@Valid @RequestBody Game game) throws URISyntaxException {
        Game result = gameRepository.save(game);
        return ResponseEntity.created(new URI("/api/games/" + result.getId()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Game> updateGame(@PathVariable Long id, @Valid @RequestBody Game updatedGame) {
        Optional<Game> gameOptional = gameRepository.findById(id);
        if (gameOptional.isPresent()) {
            Game game = gameOptional.get();
            game.setName(updatedGame.getName());
            game.setTimeRound(updatedGame.getTimeRound());
            game.setTimeGame(updatedGame.getTimeGame());
            game.setMinPlayers(updatedGame.getMinPlayers());
            game.setMaxPlayers(updatedGame.getMaxPlayers());
            game.setPlayer(updatedGame.getPlayer());
            gameRepository.save(game);
            return ResponseEntity.ok().body(game);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGame(@PathVariable Long id) {
        gameRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
