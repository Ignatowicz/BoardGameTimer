package com.studio2.bgt.model.repository;

import com.studio2.bgt.model.entity.Player;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PlayerRepository extends CrudRepository<Player, Long> {

    Player findPlayerById(Long id);

    Optional<Player> findPlayerByEmailAndPassword(String email, String password);
}
