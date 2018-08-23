package com.simplecasino.gameservice.service;

import com.simplecasino.gameservice.dao.GameDao;
import com.simplecasino.gameservice.dto.BalanceResponse;
import com.simplecasino.gameservice.dto.BetResponse;
import com.simplecasino.gameservice.dto.PlaceBetRequest;
import com.simplecasino.gameservice.dto.UpdateBalanceRequest;
import com.simplecasino.gameservice.exception.RestApiException;
import com.simplecasino.gameservice.model.Game;
import com.simplecasino.gameservice.model.PlayerBet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GameServiceImplTest {

    private static final Long TEST_ID = 1L;

    @Mock
    private GameDao gameDao;

    @Mock
    private WalletServiceProxy walletService;

    @InjectMocks
    private GameServiceImpl gameService;

    @Test
    public void saveGame_ifIdNotExist_thenReturnSavedGame() {
        when(gameDao.existsById(TEST_ID)).thenReturn(false);

        Game game = new Game(TEST_ID);
        when(gameDao.save(game)).thenReturn(game);

        Game returnedGame = gameService.saveGame(game);
        assertEquals(game, returnedGame);
    }

    @Test(expected = RestApiException.class)
    public void saveGame_ifIdAlreadyExist_thenThrowRestApiExceptionException() {
        when(gameDao.existsById(TEST_ID)).thenReturn(true);

        Game game = new Game(TEST_ID);
        gameService.saveGame(game);
    }

    @Test
    public void getPlayerBets_IfNoGamesWithThisPlayer_thenReturnEmptyList() {
        when(gameDao.findByPlayerId(TEST_ID)).thenReturn(Collections.emptyList());

        assertThat(gameService.getPlayerBets(TEST_ID), is(empty()));
    }

    @Test
    public void getPlayerBets_IfPresentGamesWithThisPlayer_thenReturnCorrectBetResponse() {
        PlayerBet playerBet1 = new PlayerBet(2L, BigDecimal.TEN);
        PlayerBet playerBet2 = new PlayerBet(2L, BigDecimal.ONE);
        PlayerBet playerBet3 = new PlayerBet(TEST_ID, BigDecimal.ZERO);
        PlayerBet playerBet4 = new PlayerBet(TEST_ID, BigDecimal.TEN);

        Game game1 = new Game(TEST_ID);
        game1.setPlayerBets(Arrays.asList(playerBet1, playerBet2, playerBet3));

        Long secondGameID = 5L;
        Game game2 = new Game(secondGameID);
        game2.setPlayerBets(Arrays.asList(playerBet2, playerBet3, playerBet3, playerBet4));

        when(gameDao.findByPlayerId(TEST_ID)).thenReturn(Arrays.asList(game1, game2));

        List<BetResponse> betResponses = gameService.getPlayerBets(TEST_ID);

        assertEquals(2, betResponses.size());

        BetResponse betResponse1 = betResponses.stream()
                .filter(betResponse -> betResponse.getGameId().equals(TEST_ID))
                .findFirst().get();
        assertThat(betResponse1.getBets(), containsInAnyOrder(BigDecimal.ZERO));

        BetResponse betResponse2 = betResponses.stream()
                .filter(betResponse -> betResponse.getGameId().equals(secondGameID))
                .findFirst().get();
        assertThat(betResponse2.getBets(), containsInAnyOrder(BigDecimal.TEN, BigDecimal.ZERO, BigDecimal.ZERO));
    }

    @Test(expected = RestApiException.class)
    public void placeBet_ifGameNotExist_thenThrowRestApiExceptionException() {
        when(gameDao.findById(TEST_ID)).thenReturn(Optional.empty());

        PlaceBetRequest placeBetRequest = new PlaceBetRequest();
        placeBetRequest.setAmount(BigDecimal.TEN);

        gameService.placeBet(TEST_ID, placeBetRequest);
    }

    @Test
    public void placeBet_GameExist_thenReturnCorrectUpdatedPlayerBalance() {
        Game game = new Game(TEST_ID);
        when(gameDao.findById(TEST_ID)).thenReturn(Optional.of(game));

        PlaceBetRequest placeBetRequest = new PlaceBetRequest();
        placeBetRequest.setPlayerId(TEST_ID);
        placeBetRequest.setAmount(BigDecimal.TEN);

        BalanceResponse balanceResponse = new BalanceResponse(BigDecimal.ONE);
        when(walletService.updateBalance(any(), any(UpdateBalanceRequest.class))).thenReturn(balanceResponse);

        BalanceResponse returnedBalanceResponce = gameService.placeBet(TEST_ID, placeBetRequest);
        assertEquals(balanceResponse, returnedBalanceResponce);
    }
}
