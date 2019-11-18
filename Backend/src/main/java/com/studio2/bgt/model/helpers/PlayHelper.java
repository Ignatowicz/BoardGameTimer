package com.studio2.bgt.model.helpers;

import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PlayHelper {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long playId;

    private Long gameId;
    private Long playerId;
    private String gameName;
    private int timeRound;
    private int timeGame;
    private int minPlayers;
    private int maxPlayers;
    private Set<Long> friends;

}
