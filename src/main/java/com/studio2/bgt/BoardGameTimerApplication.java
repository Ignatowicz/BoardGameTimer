package com.studio2.bgt;

import com.studio2.bgt.model.entity.Game;
import com.studio2.bgt.model.entity.Player;
import com.studio2.bgt.model.repository.PlayRepository;
import com.studio2.bgt.model.repository.PlayerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

@Controller
@SpringBootApplication
public class BoardGameTimerApplication {

    @Resource(name = "playRepository")
    private PlayRepository playRepository;

    public static void main(String[] args) {
        SpringApplication.run(BoardGameTimerApplication.class, args);
    }

    @RequestMapping("/")
    @ResponseBody
    String home() {
        return "Hello World!";
    }

    // init db
    @Bean
    public CommandLineRunner demo(PlayerRepository playerRepository) {
        return (args) -> {

            playRepository.playId = 0L;

            // init games
            Set<Game> games1 = new HashSet<>();
            games1.add(Game.builder().name("Terraformacja Marsa").timeRound(120).timeGame(1800).minPlayers(1).maxPlayers(5).build());
            games1.add(Game.builder().name("Osadnicy").timeRound(90).timeGame(1200).minPlayers(2).maxPlayers(4).build());

            Set<Game> games2 = new HashSet<>();
            games2.add(Game.builder().name("Terra Mystica").timeRound(120).timeGame(1800).minPlayers(2).maxPlayers(5).build());
            games2.add(Game.builder().name("Osadnicy").timeRound(90).timeGame(1200).minPlayers(2).maxPlayers(4).build());

            // init players
            Player player1 = new Player("Jan", "jan.kowalski@gmail.com", "admin", games1);
            Player player2 = new Player("Bartosz", "bartosz.nowak@gmail.com", "admin", games2);
            Player player3 = new Player("Kamil", "kamil.wieckowski@gmail.com", "admin", null);
            playerRepository.save(player1);
            playerRepository.save(player2);
            playerRepository.save(player3);

        };
    }
}