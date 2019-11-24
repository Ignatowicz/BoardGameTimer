package com.studio2.bgt.controller;

import com.studio2.bgt.model.entity.Game;
import com.studio2.bgt.model.entity.Player;
import com.studio2.bgt.model.helpers.PlayHelper;
import com.studio2.bgt.model.repository.GameRepository;
import com.studio2.bgt.model.repository.PlayRepository;
import com.studio2.bgt.model.repository.PlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/play")
@CrossOrigin
public class PlayController extends AbstractController {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository gameRepository;

    @Resource(name = "playRepository")
    private PlayRepository playRepository;

    private Long getNextPlayId() {
        return ++playRepository.playId;
    }

    // main screen, on tap the row with game
    @GetMapping("/{gameId}")
    public ResponseEntity<?> play(@PathVariable Long gameId) {
        Optional<Game> gameOptional = gameRepository.findById(gameId);
        if (gameOptional.isPresent()) {
            Game game = gameOptional.get();

            Set<Player> friends = new HashSet<>();
            Player player = game.getPlayer();
            friends.addAll(player.getFriend1());
            friends.addAll(player.getFriend2());

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

            // save
            playRepository.createPlay(play);
            return ResponseEntity.ok().body(play);
        }
        return ResponseEntity.notFound().eTag("GAME NOT FOUND").build();
    }

    // button -> start game, send invitations to friends
    @PostMapping("/{playId}/startGame")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> startGame(@Valid @RequestBody Long playId, HashSet<Long> playersId) {
        PlayHelper play = playRepository.findPlayById(playId);
        if (play == null) {
            return ResponseEntity.notFound().build();
        }

        Set<Player> friends = (HashSet<Player>) playerRepository.findAll();
        play.setFriends(friends.stream().filter(p -> playersId.contains(p.getId())).collect(Collectors.toSet()));

        // save
        playRepository.findPlayById(playId).setFriends(play.getFriends());

//        play.getFriend().forEach();  // TODO: sent requests to all friends

        return ResponseEntity.ok().body("Start game requests sent to your friends!");
    }

    // button in notification box -> change to text "Wait for others players to start a game"
    @GetMapping("/{playId}/acceptGame/{playerId}")
    public ResponseEntity<?> acceptGame(@PathVariable Long playId, Long playerId) {
        PlayHelper play = playRepository.findPlayById(playId);
        if (play == null) {
            return ResponseEntity.notFound().build();
        }

        play.getAccepted().add(playerRepository.findPlayerById(playerId));

        // save
        playRepository.findPlayById(playId).setAccepted(play.getAccepted());

        if (play.getAccepted().equals(play.getFriends())) {

            // set player for tour A & save
            play.getFriends().forEach(f -> playRepository.findPlayById(playId).getPlayersTourA().add(f));

            // TODO: broadcast to all about game start
            // TODO: send message to first player in queue playerTourA

            return ResponseEntity.ok().body("All players have accepted the game!");
        }

        return ResponseEntity.ok().body("Player: " + playerRepository.findPlayerById(playerId).getName() + " has accepted the game!");
    }

    @GetMapping("/{playId}/rejectGame/{playerId}")
    public ResponseEntity<?> rejectGame(@PathVariable Long playId, Long playerId) {
        PlayHelper play = playRepository.findPlayById(playId);
        if (play == null) {
            return ResponseEntity.notFound().build();
        }

        playRepository.deletePlay(playId);

        //TODO: notification to game starter about game rejection (playerStarterGameId)

        return ResponseEntity.ok().body("Player: " + playerRepository.findPlayerById(playerId).getName() + " has rejected the game!");
    }

    @GetMapping("/{playId}/tour/{isTourA}/endRound/{playerId}")
    public ResponseEntity<?> endRound(@PathVariable Long playId, Boolean isTourA, Long playerId) {
        PlayHelper play = playRepository.findPlayById(playId);
        if (play == null) {
            return ResponseEntity.notFound().build();
        }

        Player actualPlayer = new Player();
        Player nextPlayer = new Player();

        if (isTourA) {
            actualPlayer = play.getPlayersTourA().remove();
            nextPlayer = play.getPlayersTourA().peek();

            // TODO: sent notification to nextPlayer, and start counting time for him

            play.getPlayersTourB().add(actualPlayer);

            // save
            playRepository.updatePlay(play);

            // end tour if all players ended their round
            if (play.getPlayersTourA().isEmpty()) {
                return endTour(play, isTourA);
            }
        } else {
            actualPlayer = play.getPlayersTourB().remove();
            nextPlayer = play.getPlayersTourB().peek();

            // TODO: sent notification to nextPlayer, and start counting time for him

            play.getPlayersTourA().add(actualPlayer);

            // save
            playRepository.updatePlay(play);

            // end tour if all players ended their round
            if (play.getPlayersTourB().isEmpty()) {
                return endTour(play, isTourA);
            }
        }

        return ResponseEntity.ok().body("Player: " + actualPlayer.getName() + " has ended the round!");
    }

    private ResponseEntity<?> endTour(PlayHelper play, Boolean isTourA) {

        // change tour
        isTourA = !isTourA;

        play.setTourA(isTourA);


        Player actualPlayerz = new Player();

        if (isTourA) {
            actualPlayerz = play.getPlayersTourA().peek();
            // TODO: send notification to first player in queue of playersTourA
        } else {
            actualPlayerz = play.getPlayersTourB().peek();
            // TODO: send notification to first player in queue of playersTourB
        }

        playRepository.updatePlay(play);

        return ResponseEntity.ok().body("Tour ended. Player " + actualPlayerz + " starts his round");
    }

    @GetMapping("/{playId}/pause")
    public ResponseEntity<?> pause(@PathVariable Long playId) {
        PlayHelper play = playRepository.findPlayById(playId);
        if (play == null) {
            return ResponseEntity.notFound().build();
        }

        //TODO: broadcast notification to all about game pause
        // play.getFriends()

        return ResponseEntity.ok().body("Game paused");
    }

    @GetMapping("/{playId}/resume")
    public ResponseEntity<?> resume(@PathVariable Long playId) {
        PlayHelper play = playRepository.findPlayById(playId);
        if (play == null) {
            return ResponseEntity.notFound().build();
        }

        //TODO: broadcast notification to all about game resume
        // play.getFriends()

        return ResponseEntity.ok().body("Game resumed");
    }

    // only actual player has a button to end the game
    @GetMapping("/{playId}/endGame")
    public ResponseEntity<?> endGame(@PathVariable Long playId) {
        PlayHelper play = playRepository.findPlayById(playId);
        if (play == null) {
            return ResponseEntity.notFound().build();
        }

        //TODO: broadcast notification to all about game end
        // play.getFriends()

        playRepository.deletePlay(playId);

        return ResponseEntity.ok().body("Game resumed");
    }

}
