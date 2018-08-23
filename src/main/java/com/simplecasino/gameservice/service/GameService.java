package com.simplecasino.gameservice.service;

import com.simplecasino.gameservice.dto.BalanceResponse;
import com.simplecasino.gameservice.dto.BetResponse;
import com.simplecasino.gameservice.dto.PlaceBetRequest;
import com.simplecasino.gameservice.model.Game;

import java.util.List;

public interface GameService {

    Game saveGame(Game game);

    List<BetResponse> getPlayerBets(Long playerId);

    BalanceResponse placeBet(Long gameId, PlaceBetRequest placeBetRequest);
}
