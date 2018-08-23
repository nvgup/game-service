package com.simplecasino.gameservice.service;

import com.simplecasino.gameservice.dao.GameDao;
import com.simplecasino.gameservice.dto.BalanceResponse;
import com.simplecasino.gameservice.dto.BetResponse;
import com.simplecasino.gameservice.dto.PlaceBetRequest;
import com.simplecasino.gameservice.dto.UpdateBalanceRequest;
import com.simplecasino.gameservice.exception.RestApiException;
import com.simplecasino.gameservice.model.Game;
import com.simplecasino.gameservice.model.PlayerBet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public void saveGame(Game game) {
        throwExceptionIfGameExists(game.getId());
        gameDao.save(game);
    }

    private void throwExceptionIfGameExists(Long gameId) {
        if (gameDao.existsById(gameId)) {
            throw new RestApiException(RestApiException.Type.GAME_ALREADY_EXIST);
        }
    }

    @Override
    public List<BetResponse> getPlayerBets(Long playerId) {
        List<Game> games = gameDao.findByPlayerId(playerId);

        return games.stream()
                .map(game -> new BetResponse(
                        game.getId(),
                        game.getPlayerBets().stream()
                                .filter(playerBet -> playerId.equals(playerBet.getPlayerId()))
                                .map(PlayerBet::getAmount)
                                .collect(Collectors.toList())
                )).collect(Collectors.toList());
    }

    @Override
    public BalanceResponse placeBet(Long gameId, PlaceBetRequest placeBetRequest) {
        UpdateBalanceRequest updateBalanceRequest = new UpdateBalanceRequest();
        updateBalanceRequest.setBalance(placeBetRequest.getAmount().negate());

        Game game = gameDao.findById(gameId)
                .orElseThrow(() -> new RestApiException(RestApiException.Type.GAME_NOT_FOUND));

        BalanceResponse balanceResponse = walletService.updateBalance(placeBetRequest.getPlayerId(), updateBalanceRequest);

        PlayerBet playerBet = new PlayerBet(placeBetRequest.getPlayerId(), placeBetRequest.getAmount());
        game.addPlayerBet(playerBet);

        gameDao.save(game);

        return balanceResponse;
    }
}
