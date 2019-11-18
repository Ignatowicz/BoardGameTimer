package com.studio2.bgt.model.repository;

import com.studio2.bgt.model.entity.Player;
import org.springframework.data.repository.CrudRepository;

public interface PlayerRepository extends CrudRepository<Player, Long> {

    Player findPlayerById(Long id);
}
