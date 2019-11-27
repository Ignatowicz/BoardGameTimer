package com.studio2.bgt.controller;

import com.studio2.bgt.model.entity.Game;
import com.studio2.bgt.model.entity.Player;
import com.studio2.bgt.model.enums.SendTo;
import com.studio2.bgt.model.helpers.NotificationHelper;
import com.studio2.bgt.model.helpers.PlayHelper;
import com.studio2.bgt.model.helpers.StartGameHelper;
import com.studio2.bgt.model.repository.GameRepository;
import com.studio2.bgt.model.repository.PlayRepository;
import com.studio2.bgt.model.repository.PlayerRepository;
import com.studio2.bgt.notification.NotificationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;
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

    private NotificationManager notificationManager = new NotificationManager();

    private Long getNextPlayId() {
        return ++playRepository.playId;
    }

    // main screen, request onTap the row with game name
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
                    .playerGameStarterId(null)
                    .isTourA(true)
                    .gameName(game.getName())
                    .timeRound(game.getTimeRound())
                    .timeGame(game.getTimeGame())
                    .minPlayers(game.getMinPlayers())
                    .maxPlayers(game.getMaxPlayers())
                    .friends(friends)
                    .accepted(new HashSet<>())
                    .playersTourA(new LinkedList<>())
                    .playersTourB(new LinkedList<>())
                    .build();


            // clear friends' friends
            clearResponse(play);

            // save
            playRepository.createPlay(play);
            return ResponseEntity.ok().body(play);
        }
        return ResponseEntity.notFound().eTag("GAME NOT FOUND").build();
    }

    // button -> start game, send invitations to friends
    @PostMapping("/startGame")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> startGame(@Valid @RequestBody StartGameHelper startGame) {
        PlayHelper play = playRepository.findPlayById(startGame.getPlayId());
        if (play == null) {
            return ResponseEntity.notFound().build();
        }

        // set players from provided friend ids
        Set<Player> friends = new HashSet<>();
        playerRepository.findAll().forEach(friends::add);
        play.setFriends(friends.stream().filter(p -> startGame.getPlayersId().contains(p.getId())).collect(Collectors.toSet()));

        // clear friends' friends
        clearResponse(play);

        // save
        playRepository.findPlayById(startGame.getPlayId()).setFriends(play.getFriends());

        // send request for game to all friends (players in near future)
        NotificationHelper notificationHelper = preparePlayersToWhomNotificationWillBeSent(play, SendTo.FRIENDS);
        List<String> notification = notificationManager
                .sendNotification(1L, notificationHelper.getTopics(),
                        "Invitation to game " + play.getGameName(),
                        "Open the app and accept the invitation!",
                        notificationHelper.getPlayers());
        log.info(String.valueOf(notification));

        return ResponseEntity.ok().body("Start game requests sent to your friends!");
    }

    // button in notification box -> change to text "Wait for others players to start a game"
    @GetMapping("/{playId}/acceptGame/{playerId}")
    public ResponseEntity<?> acceptGame(@PathVariable Long playId, @PathVariable Long playerId) {
        PlayHelper play = playRepository.findPlayById(playId);
        if (play == null) {
            return ResponseEntity.notFound().build();
        }

        play.getAccepted().add(playerRepository.findPlayerById(playerId));

        // clear friends' friends
        clearResponse(play);

        // save
        playRepository.findPlayById(playId).setAccepted(play.getAccepted());

        if (play.getAccepted().size() == play.getFriends().size()) {

            // set player for tour A & save
            play.getFriends().forEach(f -> playRepository.findPlayById(playId).getPlayersTourA().add(f));

            // send notification to all players about game start
            NotificationHelper notificationHelper = preparePlayersToWhomNotificationWillBeSent(play, SendTo.PLAYERS_TOUR_A);
            List<String> notification = notificationManager
                    .sendNotification(2L, notificationHelper.getTopics(),
                            "The game has started!",
                            "All the players have accepted the invitation.\nIt's " + play.getPlayersTourA().peek().getName() + " turn!",
                            notificationHelper.getPlayers());
            log.info(String.valueOf(notification));

            // notify first player about his turn
            NotificationHelper notificationHelper1 = preparePlayersToWhomNotificationWillBeSent(play, SendTo.FIRST_PLAYER_TOUR_A);
            notifyFirstPlayerAboutTurnStart(notificationHelper1);

            return ResponseEntity.ok().body("All players have accepted the game!");
        }

        return ResponseEntity.ok().body("Player: " + playerRepository.findPlayerById(playerId).getName() + " has accepted the game!");
    }

    private void notifyFirstPlayerAboutTurnStart(NotificationHelper notificationHelper) {
        List<String> notification = notificationManager
                .sendNotification(3L, notificationHelper.getTopics(),
                        "It's your turn!",
                        "Your round time has just begun!",
                        notificationHelper.getPlayers());
        log.info(String.valueOf(notification));
    }

    @GetMapping("/{playId}/rejectGame/{playerId}")
    public ResponseEntity<?> rejectGame(@PathVariable Long playId, @PathVariable Long playerId) {
        PlayHelper play = playRepository.findPlayById(playId);
        if (play == null) {
            return ResponseEntity.notFound().build();
        }

        playRepository.deletePlay(playId);

        // notify about cancel game start
        NotificationHelper notificationHelper = preparePlayersToWhomNotificationWillBeSent(play, SendTo.FRIENDS);
        List<String> notification = notificationManager
                .sendNotification(4L, notificationHelper.getTopics(),
                        "The game " + play.getGameName() + " canceled.",
                        playerRepository.findPlayerById(playerId).getName() + " rejected to play.",
                        notificationHelper.getPlayers());
        log.info(String.valueOf(notification));

        return ResponseEntity.ok().body("Player: " + playerRepository.findPlayerById(playerId).getName() + " has rejected the game!");
    }

    @GetMapping("/{playId}/tour/{isTourA}/endRound")
    public ResponseEntity<?> endRound(@PathVariable Long playId, @PathVariable Boolean isTourA) {
        PlayHelper play = playRepository.findPlayById(playId);
        if (play == null) {
            return ResponseEntity.notFound().build();
        }

        Player actualPlayer;

        if (isTourA) {
            actualPlayer = play.getPlayersTourA().remove();

            // notify next player about his turn
            NotificationHelper notificationHelper = preparePlayersToWhomNotificationWillBeSent(play, SendTo.FIRST_PLAYER_TOUR_A);
            notifyFirstPlayerAboutTurnStart(notificationHelper);

            play.getPlayersTourA().add(actualPlayer);

            // clear friends' friends
            clearResponse(play);

            // save
            playRepository.updatePlay(play);
        } else {
            actualPlayer = play.getPlayersTourB().remove();

            // notify next player about his turn
            NotificationHelper notificationHelper = preparePlayersToWhomNotificationWillBeSent(play, SendTo.FIRST_PLAYER_TOUR_B);
            notifyFirstPlayerAboutTurnStart(notificationHelper);

            play.getPlayersTourB().add(actualPlayer);

            // clear friends' friends
            clearResponse(play);

            // save
            playRepository.updatePlay(play);
        }

        return ResponseEntity.ok().body("Player: " + actualPlayer.getName() + " has ended the round!");
    }

    @GetMapping("/{playId}/tour/{isTourA}/endTour")
    public ResponseEntity<?> endTour(@PathVariable Long playId, @PathVariable Boolean isTourA) {
        PlayHelper play = playRepository.findPlayById(playId);
        if (play == null) {
            return ResponseEntity.notFound().build();
        }

        Player actualPlayer;

        if (isTourA) {
            actualPlayer = play.getPlayersTourA().remove();

            // notify next player about his turn
            NotificationHelper notificationHelper = preparePlayersToWhomNotificationWillBeSent(play, SendTo.FIRST_PLAYER_TOUR_A);
            notifyFirstPlayerAboutTurnStart(notificationHelper);

            play.getPlayersTourB().add(actualPlayer);

            // clear friends' friends
            clearResponse(play);

            // save
            playRepository.updatePlay(play);

            // end tour if all players ended their round
            if (play.getPlayersTourA().isEmpty()) {
                return nextTour(play, isTourA);  // finishing tour A
            }
        } else {
            actualPlayer = play.getPlayersTourB().remove();

            // notify next player about his turn
            NotificationHelper notificationHelper = preparePlayersToWhomNotificationWillBeSent(play, SendTo.FIRST_PLAYER_TOUR_B);
            notifyFirstPlayerAboutTurnStart(notificationHelper);

            play.getPlayersTourA().add(actualPlayer);

            // clear friends' friends
            clearResponse(play);

            // save
            playRepository.updatePlay(play);

            // end tour if all players ended their round
            if (play.getPlayersTourB().isEmpty()) {
                return nextTour(play, isTourA);  // finishing tour B
            }
        }

        return ResponseEntity.ok().body("Player: " + actualPlayer.getName() + " has ended the tour!");
    }

    private ResponseEntity<?> nextTour(PlayHelper play, Boolean isTourA) {

        // change tour
        isTourA = !isTourA;

        play.setTourA(isTourA);

        // notify first player about his turn
        if (isTourA) {
            NotificationHelper notificationHelper = preparePlayersToWhomNotificationWillBeSent(play, SendTo.FIRST_PLAYER_TOUR_A);
            notifyFirstPlayerAboutTurnStart(notificationHelper);
        } else {
            NotificationHelper notificationHelper = preparePlayersToWhomNotificationWillBeSent(play, SendTo.FIRST_PLAYER_TOUR_B);
            notifyFirstPlayerAboutTurnStart(notificationHelper);
        }

        // clear friends' friends
        clearResponse(play);

        playRepository.updatePlay(play);

        return ResponseEntity.ok().body("Starting new tour.");
    }

    @GetMapping("/{playId}/pause")
    public ResponseEntity<?> pause(@PathVariable Long playId) {
        PlayHelper play = playRepository.findPlayById(playId);
        if (play == null) {
            return ResponseEntity.notFound().build();
        }

        // notify about game pause
        NotificationHelper notificationHelper = preparePlayersToWhomNotificationWillBeSent(play, SendTo.FRIENDS);
        List<String> notification = notificationManager
                .sendNotification(5L, notificationHelper.getTopics(),
                        "The game " + play.getGameName() + " has been paused",
                        "Wait to the game resume.",
                        notificationHelper.getPlayers());
        log.info(String.valueOf(notification));

        return ResponseEntity.ok().body("The game paused");
    }

    @GetMapping("/{playId}/resume")
    public ResponseEntity<?> resume(@PathVariable Long playId) {
        PlayHelper play = playRepository.findPlayById(playId);
        if (play == null) {
            return ResponseEntity.notFound().build();
        }

        // notify about game resume
        NotificationHelper notificationHelper = preparePlayersToWhomNotificationWillBeSent(play, SendTo.FRIENDS);
        List<String> notification = notificationManager
                .sendNotification(6L, notificationHelper.getTopics(),
                        "The game " + play.getGameName() + " has been resumed",
                        "Actual player has started his resumed the round.",
                        notificationHelper.getPlayers());
        log.info(String.valueOf(notification));

        return ResponseEntity.ok().body("The game resumed");
    }

    // only actual player has a button to end the game
    @GetMapping("/{playId}/endGame")
    public ResponseEntity<?> endGame(@PathVariable Long playId) {
        PlayHelper play = playRepository.findPlayById(playId);
        if (play == null) {
            return ResponseEntity.notFound().build();
        }

        // notify about game end
        NotificationHelper notificationHelper = preparePlayersToWhomNotificationWillBeSent(play, SendTo.FRIENDS);
        List<String> notification = notificationManager
                .sendNotification(7L, notificationHelper.getTopics(),
                        "The game " + play.getGameName() + " is over",
                        "Hope You enjoyed the game!",
                        notificationHelper.getPlayers());
        log.info(String.valueOf(notification));

        playRepository.deletePlay(playId);

        return ResponseEntity.ok().body("The game resumed");
    }

}
