package com.studio2.bgt.controller;

import com.studio2.bgt.model.entity.Player;
import com.studio2.bgt.model.repository.PlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/players")
@CrossOrigin
public class PlayerController {

    @Autowired
    private PlayerRepository playerRepository;

    private Player clearInfiniteFriendsLoop(Player player) {
        player.getFriend1().forEach(f -> f.setFriend1(new HashSet<>()));
        player.getFriend1().forEach(f -> f.setFriend2(new HashSet<>()));
        player.getFriend2().forEach(f -> f.setFriend1(new HashSet<>()));
        player.getFriend2().forEach(f -> f.setFriend2(new HashSet<>()));
        return player;
    }

    private Player clearResponse(Player player) {
        clearInfiniteFriendsLoop(player);
        return player;
    }

    @GetMapping
    public Iterable findAll() {
        return playerRepository.findAll();
    }

    @GetMapping("/init")
    public ResponseEntity<?> init() {
        Player player1 = playerRepository.findPlayerById(1L);
        Player player2 = playerRepository.findPlayerById(4L);
        Player player3 = playerRepository.findPlayerById(7L);
        player1.addFriend(player2);
        player1.addFriend(player3);
        playerRepository.save(player1);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        Optional<Player> player = playerRepository.findById(id);
        if (player.isPresent()) {
            clearResponse(player.get());
            return ResponseEntity.ok().body(player);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Player> createPlayer(@Valid @RequestBody Player player) throws URISyntaxException {
        Player result = playerRepository.save(player);
        clearResponse(result);
        return ResponseEntity.created(new URI("/api/players/" + result.getId()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable Long id, @Valid @RequestBody Player updatedPlayer) {
        Optional<Player> playerOptional = playerRepository.findById(id);
        if (playerOptional.isPresent()) {
            Player player = playerOptional.get();
            player.setName(updatedPlayer.getName());
            player.setEmail(updatedPlayer.getEmail());
            player.setPassword(updatedPlayer.getPassword());
            player.setGames(updatedPlayer.getGames());
            player.setFriend1(updatedPlayer.getFriend1());
            player.setFriend2(updatedPlayer.getFriend2());
            playerRepository.save(player);
            clearResponse(player);
            return ResponseEntity.ok().body(player);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlayer(@PathVariable Long id) {
        playerRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
