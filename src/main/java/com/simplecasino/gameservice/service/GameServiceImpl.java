package com.simplecasino.gameservice.service;

import com.simplecasino.gameservice.dao.GameDao;
import com.simplecasino.gameservice.dto.BalanceResponse;
import com.simplecasino.gameservice.dto.BetResponse;
import com.simplecasino.gameservice.dto.PlaceBetRequest;
import com.simplecasino.gameservice.dto.UpdateBalanceRequest;
import com.simplecasino.gameservice.exception.ResourceNotFoundException;
import com.simplecasino.gameservice.model.Game;
import com.simplecasino.gameservice.model.PlayerBet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameServiceImpl implements GameService {

    private GameDao gameDao;
    private WalletServiceProxy walletService;

    @Autowired
    public GameServiceImpl(GameDao gameDao, WalletServiceProxy walletService) {
        this.gameDao = gameDao;
        this.walletService = walletService;
    }

    @Transactional
    @Override
    public void saveGame(Game game) {
        gameDao.save(game);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BetResponse> getPlayerBets(Long playerId) {
        List<Game> games = gameDao.findByPlayerId(playerId);
        return games.stream()
                .map(game -> new BetResponse(game.getId(), game.getPlayerBets().stream()
                        .map(PlayerBet::getAmount)
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public BalanceResponse placeBet(Long gameId, PlaceBetRequest placeBetRequest) {
        UpdateBalanceRequest updateBalanceRequest = new UpdateBalanceRequest();
        updateBalanceRequest.setBalance(placeBetRequest.getAmount().negate());

        BalanceResponse balanceResponse = walletService.updateBalance(placeBetRequest.getPlayerId(), updateBalanceRequest);
        if (!balanceResponse.getError().isPresent()) {
            Game game = gameDao.findById(gameId)
                    .orElseThrow(() -> new ResourceNotFoundException(String.format("Game with id '%s' wan not found", gameId)));

            PlayerBet playerBet = new PlayerBet(placeBetRequest.getPlayerId(), placeBetRequest.getAmount());
            game.addPlayerBet(playerBet);
            gameDao.save(game);
        }

        return balanceResponse;
    }
}
