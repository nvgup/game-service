package com.simplecasino.gameservice.dao;

import com.simplecasino.gameservice.model.Game;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface GameDao extends MongoRepository<Game, Long> {

    @Query(value = "{'playerBets.playerId' : ?0 }", fields = "{ 'playerBets.playerId' : 0 }")
    List<Game> findByPlayerId(Long playerId);
}