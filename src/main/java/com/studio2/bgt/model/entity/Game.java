package com.studio2.bgt.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "game_id")
    private Long id;

    @Column(nullable = false, name = "name")
    private String name;

    @Column(nullable = false, name = "time_round")
    private int time_round;

    @Column(nullable = false, name = "time_game")
    private int time_game;

    @Column(nullable = false, name = "min_players")
    private int min_players;

    @Column(nullable = false, name = "max_players")
    private int max_players;

    @ManyToOne
    @JoinColumn(name = "player_id")
    @JsonBackReference
    private Player player;

}
