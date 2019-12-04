package com.studio2.bgt.model.helpers;

import com.studio2.bgt.model.entity.Player;
import lombok.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

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

}
