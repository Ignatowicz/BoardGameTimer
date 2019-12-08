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
import org.json.JSONObject;
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

    @GetMapping("/{playId}")
    public ResponseEntity<?> getActualPlay(@PathVariable Long playId) {
        log.info("Called: getActualPlay with playId " + playId);
        PlayHelper play = playRepository.findPlayById(playId);

        log.info("found playId "+ playId);
        play.getPlayersTourA().forEach(f -> log.info("PlayerTourA" + f.getName()));

        return ResponseEntity.ok().body(play);
    }

    @PutMapping("/{playId}")
    public ResponseEntity<PlayHelper> updatePlay(@PathVariable Long playId, @Valid @RequestBody PlayHelper updatedPlay) {
        log.info("Called: updatePlay with playId " + playId + " and updatedPlay: " + updatedPlay.toString());
        PlayHelper play = playRepository.findPlayById(playId);
        play.setPlayId(updatedPlay.getPlayId());
        play.setGameId(updatedPlay.getGameId());
        play.setPlayerId(updatedPlay.getPlayerId());
        play.setPlayerGameStarterId(updatedPlay.getPlayerGameStarterId());
        play.setTourA(updatedPlay.isTourA());
        play.setGameName(updatedPlay.getGameName());
        play.setTimeRound(updatedPlay.getTimeRound());
        play.setTimeGame(updatedPlay.getTimeGame());
        play.setMinPlayers(updatedPlay.getMinPlayers());
        play.setMaxPlayers(updatedPlay.getMaxPlayers());
        play.setFriends(updatedPlay.getFriends());
        play.setAccepted(updatedPlay.getAccepted());
        play.setPlayersTourA(updatedPlay.getPlayersTourA());
        play.setPlayersTourB(updatedPlay.getPlayersTourB());
        play.setRoundTimePlayers(updatedPlay.getRoundTimePlayers());
        play.setGameTimePlayers(updatedPlay.getGameTimePlayers());

        // save
        playRepository.updatePlay(play);
        play.getPlayersTourA().forEach(f -> log.info("PlayerTourA" + f.getName()));

        return ResponseEntity.ok().body(play);
    }

    // main screen, request onTap the row with game name
    @GetMapping("/game/{gameId}")
    public ResponseEntity<?> play(@PathVariable Long gameId) {
        log.info("Called: play with gameId " + gameId);
        Optional<Game> gameOptional = gameRepository.findById(gameId);
        if (gameOptional.isPresent()) {
            Game game = gameOptional.get();

            Set<Player> friends = new HashSet<>();
            Player player = game.getPlayer();
            friends.addAll(player.getFriend1());
            friends.addAll(player.getFriend2());
            friends.add(game.getPlayer());

            Map<Long, Long> roundTimePlayers = new HashMap<>();
            Map<Long, Long> gameTimePlayers = new HashMap<>();

            friends.forEach(f -> roundTimePlayers.put(f.getId(), (long) game.getTimeRound()));
            friends.forEach(f -> gameTimePlayers.put(f.getId(), (long) game.getTimeGame()));

            PlayHelper play = PlayHelper.builder()
                    .playId(getNextPlayId())
                    .gameId(gameId)
                    .playerId(game.getPlayer().getId())
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
                    .roundTimePlayers(roundTimePlayers)
                    .gameTimePlayers(gameTimePlayers)
                    .build();

            // clear friends' friends
            clearResponse(play);

            log.info("Created new play");

            // save
            playRepository.createPlay(play);

            log.info("playid=" + play.getPlayId());
            log.info("Saved play with palyid= " + playRepository.findPlayById(play.getPlayId()).getPlayId());

            return ResponseEntity.ok().body(play);
        }
        return ResponseEntity.notFound().eTag("GAME NOT FOUND").build();
    }

    // button -> start game, send invitations to friends
    @PostMapping("/startGame/{playerId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> startGame(@Valid @PathVariable Long playerId, @RequestBody StartGameHelper startGame) {
        log.info("Called: startGame with playerId " + playerId + " with startGame: " + startGame.toString());
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
        NotificationHelper notificationHelper = prepareNotification(playerRepository.findPlayerById(playerId), play, SendTo.FRIENDS);
        List<String> notification = notificationManager
                .sendNotification(1L, notificationHelper.getTopics(),
                        "Invitation to game " + play.getGameName(),
                        "Open the app and accept the invitation!",
                        notificationHelper.getPlayers(),
                        play.getPlayId().toString()
                );
        log.info(String.valueOf(notification));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("response", "Start game requests sent to your friends!");
        return ResponseEntity.ok().body(jsonObject);
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

            // notify first player about his turn
            NotificationHelper notificationHelperFirstPlayer = prepareNotification(playerRepository.findPlayerById(playerId), play, SendTo.FIRST_PLAYER_TOUR_A);
            notifyFirstPlayerAboutTurnStart(notificationHelperFirstPlayer);

//            // notify other players about next player turn
//            NotificationHelper notificationHelperAllExceptFirstPlayer = prepareNotification(playerRepository.findPlayerById(playerId), play, SendTo.ALL_EXCEPT_FIRST_PLAYER_A);
//            notifyOtherPlayersAboutFirstPlayerTurnStart(notificationHelperAllExceptFirstPlayer);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("response", "All players have accepted the game!");
            return ResponseEntity.ok().body(jsonObject);
        }
//        else {
//            // send notification to all players about game start
//            NotificationHelper notificationHelper = prepareNotification(playerRepository.findPlayerById(playerId), play, SendTo.ACCEPTED);
//            List<String> notification = notificationManager
//                    .sendNotification(2L, notificationHelper.getTopics(),
//                            playerRepository.findPlayerById(playerId) + " has accepted the game!",
//                            "Wait till all players accept the invitation.",
//                            notificationHelper.getPlayers(),
//                            play.getPlayId().toString()
//                    );
//            log.info(String.valueOf(notification));
//        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("response", "Player: " + playerRepository.findPlayerById(playerId).getName() + " has accepted the game!");
        return ResponseEntity.ok().body(jsonObject);
    }

    private void notifyFirstPlayerAboutTurnStart(NotificationHelper notificationHelper) {
        List<String> notification = notificationManager
                .sendNotification(4L, notificationHelper.getTopics(),
                        "It's your turn!",
                        "Your round time has just begun!",
                        notificationHelper.getPlayers(),
                        notificationHelper.getPlay().getPlayId().toString()
                );
        log.info(String.valueOf(notification));
    }

    private void notifyOtherPlayersAboutFirstPlayerTurnStart(NotificationHelper notificationHelper) {
        List<String> notification = notificationManager
                .sendNotification(5L, notificationHelper.getTopics(),
                        "It's " + notificationHelper.getPlayer().getName() + " turn!",
                        notificationHelper.getPlayer().getName() + "'s round time has just begun!",
                        notificationHelper.getPlayers(),
                        notificationHelper.getPlay().getPlayId().toString()
                );
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
        NotificationHelper notificationHelper = prepareNotification(playerRepository.findPlayerById(playerId), play, SendTo.FRIENDS);
        List<String> notification = notificationManager
                .sendNotification(3L, notificationHelper.getTopics(),
                        "The game " + play.getGameName() + " canceled.",
                        playerRepository.findPlayerById(playerId).getName() + " rejected to play.",
                        notificationHelper.getPlayers(),
                        play.getPlayId().toString()
                );
        log.info(String.valueOf(notification));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("response", "Player: " + playerRepository.findPlayerById(playerId).getName() + " has rejected the game!");
        return ResponseEntity.ok().body(jsonObject);
    }

    @GetMapping("/{playId}/tour/{isTourA}/endRound/{playerId}")
    public ResponseEntity<?> endRound(@PathVariable Long playId, @PathVariable Boolean isTourA, @PathVariable Long playerId) {
        PlayHelper play = playRepository.findPlayById(playId);
        if (play == null) {
            return ResponseEntity.notFound().build();
        }

        Player actualPlayer;

        if (isTourA) {
            actualPlayer = play.getPlayersTourA().remove();
            play.getPlayersTourA().add(actualPlayer);

            // clear friends' friends
            clearResponse(play);

            // save
            playRepository.updatePlay(play);

            System.out.println("\n\n");
            play.getPlayersTourA().forEach(f -> System.out.println(f.getName()));

            System.out.println("\n\nbaza");
            playRepository.findPlayById(playId).getPlayersTourA().forEach(f -> System.out.println(f.getName()));

            // notify next player about his turn
            NotificationHelper notificationHelperFirstPlayer = prepareNotification(playerRepository.findPlayerById(playerId), play, SendTo.FIRST_PLAYER_TOUR_A);
            notifyFirstPlayerAboutTurnStart(notificationHelperFirstPlayer);

//            // notify other players about next player turn
//            NotificationHelper notificationHelperAllExceptFirstPlayer = prepareNotification(playerRepository.findPlayerById(playerId), play, SendTo.ALL_EXCEPT_FIRST_PLAYER_A);
//            notifyOtherPlayersAboutFirstPlayerTurnStart(notificationHelperAllExceptFirstPlayer);
        } else {
            actualPlayer = play.getPlayersTourB().remove();
            play.getPlayersTourB().add(actualPlayer);

            // clear friends' friends
            clearResponse(play);

            // save
            playRepository.updatePlay(play);

            // notify next player about his turn
            NotificationHelper notificationHelperFirstPlayer = prepareNotification(playerRepository.findPlayerById(playerId), play, SendTo.FIRST_PLAYER_TOUR_B);
            notifyFirstPlayerAboutTurnStart(notificationHelperFirstPlayer);

//            // notify other players about next player turn
//            NotificationHelper notificationHelperAllExceptFirstPlayer = prepareNotification(playerRepository.findPlayerById(playerId), play, SendTo.ALL_EXCEPT_FIRST_PLAYER_B);
//            notifyOtherPlayersAboutFirstPlayerTurnStart(notificationHelperAllExceptFirstPlayer);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("response", "Player: " + actualPlayer.getName() + " has ended the round!");
        return ResponseEntity.ok().body(jsonObject);
    }

    @GetMapping("/{playId}/tour/{isTourA}/endTour/{playerId}")
    public ResponseEntity<?> endTour(@PathVariable Long playId, @PathVariable Boolean isTourA, @PathVariable Long playerId) {
        PlayHelper play = playRepository.findPlayById(playId);
        if (play == null) {
            return ResponseEntity.notFound().build();
        }

        Player actualPlayer;

        if (isTourA) {
            actualPlayer = play.getPlayersTourA().remove();

            play.getPlayersTourB().add(actualPlayer);

            // clear friends' friends
            clearResponse(play);

            // save
            playRepository.updatePlay(play);

            // end tour if all players ended their round
            if (play.getPlayersTourA().isEmpty()) {
                return nextTour(play, isTourA, playerId);  // finishing tour A
            }

            // notify next player about his turn
            NotificationHelper notificationHelper = prepareNotification(playerRepository.findPlayerById(playerId), play, SendTo.FIRST_PLAYER_TOUR_A);
            notifyFirstPlayerAboutTurnStart(notificationHelper);
        } else {
            actualPlayer = play.getPlayersTourB().remove();

            play.getPlayersTourA().add(actualPlayer);

            // clear friends' friends
            clearResponse(play);

            // save
            playRepository.updatePlay(play);

            // end tour if all players ended their round
            if (play.getPlayersTourB().isEmpty()) {
                return nextTour(play, isTourA, playerId);  // finishing tour B
            }

            // notify next player about his turn
            NotificationHelper notificationHelper = prepareNotification(playerRepository.findPlayerById(playerId), play, SendTo.FIRST_PLAYER_TOUR_B);
            notifyFirstPlayerAboutTurnStart(notificationHelper);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("response", "Player: " + actualPlayer.getName() + " has ended the tour!");
        return ResponseEntity.ok().body(jsonObject);
    }

    private ResponseEntity<?> nextTour(PlayHelper play, Boolean isTourA, Long playerId) {

        // change tour
        isTourA = !isTourA;

        play.setTourA(isTourA);

        // clear friends' friends
        clearResponse(play);

        playRepository.updatePlay(play);

        // notify first player about his turn
        if (isTourA) {
            NotificationHelper notificationHelper = prepareNotification(playerRepository.findPlayerById(playerId), play, SendTo.FIRST_PLAYER_TOUR_A);
            notifyFirstPlayerAboutTurnStart(notificationHelper);
        } else {
            NotificationHelper notificationHelper = prepareNotification(playerRepository.findPlayerById(playerId), play, SendTo.FIRST_PLAYER_TOUR_B);
            notifyFirstPlayerAboutTurnStart(notificationHelper);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("response", "Starting new tour.");
        return ResponseEntity.ok().body(jsonObject);
    }

    @GetMapping("/{playId}/pause/{playerId}")
    public ResponseEntity<?> pause(@PathVariable Long playId, @PathVariable Long playerId) {
        PlayHelper play = playRepository.findPlayById(playId);
        if (play == null) {
            return ResponseEntity.notFound().build();
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("response", "The game paused");
        return ResponseEntity.ok().body(jsonObject);
    }

    @GetMapping("/{playId}/resume/{playerId}")
    public ResponseEntity<?> resume(@PathVariable Long playId, @PathVariable Long playerId) {
        PlayHelper play = playRepository.findPlayById(playId);
        if (play == null) {
            return ResponseEntity.notFound().build();
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("response", "The game resumed");
        return ResponseEntity.ok().body(jsonObject);
    }

    // only actual player has a button to end the game
    @GetMapping("/{playId}/endGame/{playerId}")
    public ResponseEntity<?> endGame(@PathVariable Long playId, @PathVariable Long playerId) {
        PlayHelper play = playRepository.findPlayById(playId);
        if (play == null) {
            return ResponseEntity.notFound().build();
        }

        playRepository.deletePlay(playId);

        // notify about game end
        NotificationHelper notificationHelper = prepareNotification(playerRepository.findPlayerById(playerId), play, SendTo.FRIENDS);
        List<String> notification = notificationManager
                .sendNotification(6L, notificationHelper.getTopics(),
                        "The game " + play.getGameName() + " is over",
                        "Hope You enjoyed the game!",
                        notificationHelper.getPlayers(),
                        play.getPlayId().toString()
                );
        log.info(String.valueOf(notification));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("response", "The game is over");
        return ResponseEntity.ok().body(jsonObject);
    }

}
