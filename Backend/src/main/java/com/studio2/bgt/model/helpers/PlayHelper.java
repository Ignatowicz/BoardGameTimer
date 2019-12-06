package com.studio2.bgt.model.helpers;

import com.studio2.bgt.model.entity.Player;
import lombok.*;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PlayHelper {

    private Long playId;
    private Long gameId;
    private Long playerId;
    private Long playerGameStarterId;
    private boolean isTourA = true;
    private String gameName;
    private int timeRound;
    private int timeGame;
    private int minPlayers;
    private int maxPlayers;
    private Set<Player> friends = new HashSet<>();
    private Set<Player> accepted = new HashSet<>();
    private Queue<Player> playersTourA = new LinkedList<>();
    private Queue<Player> playersTourB = new LinkedList<>();
    private Map<Long, Long> roundTimePlayers = new HashMap<>();
    private Map<Long, Long> gameTimePlayers = new HashMap<>();

}
