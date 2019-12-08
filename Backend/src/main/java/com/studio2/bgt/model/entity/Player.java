package com.studio2.bgt.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false, name = "name")
    private String name;

    @Column(nullable = false, name = "email", unique = true)
    private String email;

    @Column(nullable = false, name = "password")
    private String password;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<Game> games;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "friends",
            joinColumns = {@JoinColumn(name = "friend1_id")},
            inverseJoinColumns = {@JoinColumn(name = "friend2_id")})
    private Set<Player> friend1 = new HashSet<Player>();

    @ManyToMany(mappedBy = "friend1")
    private Set<Player> friend2 = new HashSet<Player>();

    public Player(String name, String email, String password, Set<Game> games) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.games = games;
        if (games != null) {
            this.games.forEach(game -> game.setPlayer(this));
        }
    }

    public Player(String name, String email, String password, Set<Game> games, Set<Player> friend1) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.games = games;
        this.friend1 = friend1;
        if (games != null) {
            this.games.forEach(game -> game.setPlayer(this));
        }
    }

    public void addFriend(Player player) {
        this.friend1.add(player);
    }
}
