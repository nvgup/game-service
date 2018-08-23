package com.simplecasino.gameservice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplecasino.gameservice.dao.GameDao;
import com.simplecasino.gameservice.dto.AddGameRequest;
import com.simplecasino.gameservice.dto.BetResponse;
import com.simplecasino.gameservice.dto.PlaceBetRequest;
import com.simplecasino.gameservice.exception.RestApiException;
import com.simplecasino.gameservice.model.Game;
import com.simplecasino.gameservice.model.PlayerBet;
import com.simplecasino.gameservice.service.WalletServiceProxy;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = GameServiceApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:test.yaml")
@AutoConfigureStubRunner(ids = {"wallet-servicestubs:3333"})
public class GameControllerIntegrationTest {

    private static final Long TEST_ID = 1L;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private GameDao gameDao;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setup() {
        RestAssuredMockMvc.webAppContextSetup(context);
    }

    @Test
    public void addGame_ifIdNotExist_thenReturnHttpStatus200() throws Exception {
        AddGameRequest addGameRequest = new AddGameRequest();
        addGameRequest.setGameId(TEST_ID);

        mvc.perform(
                post("/game")
                        .content(asJsonString(addGameRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void addGame_ifIdExist_thenReturnHttpStatus409() throws Exception {
        gameDao.save(new Game(TEST_ID));

        AddGameRequest addGameRequest = new AddGameRequest();
        addGameRequest.setGameId(TEST_ID);

        mvc.perform(
                post("/game")
                        .content(asJsonString(addGameRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.CONFLICT.value()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(HttpStatus.CONFLICT.value())))
                .andExpect(jsonPath("$.message", is(RestApiException.Type.GAME_ALREADY_EXIST.getMessage())))
                .andExpect(jsonPath("$.code", is(RestApiException.Type.GAME_ALREADY_EXIST.getCode())));
    }

    @Test
    public void getPlayerBets_IfNoGamesWithThisPlayer_thenReturnHttpStatus200AndEmptyJson() throws Exception {
        mvc.perform(
                get(String.format("/games/player/%s/bets", TEST_ID))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", is(empty())));
    }

    @Test
    public void getPlayerBets_IfPresentGamesWithThisPlayer_thenReturnHttpStatus200AndCorrectBetResponse() throws Exception {
        PlayerBet playerBet1 = new PlayerBet(2L, BigDecimal.TEN);
        PlayerBet playerBet2 = new PlayerBet(2L, BigDecimal.ONE);
        PlayerBet playerBet3 = new PlayerBet(TEST_ID, BigDecimal.ZERO);
        PlayerBet playerBet4 = new PlayerBet(TEST_ID, BigDecimal.TEN);

        Game game1 = new Game(TEST_ID);
        game1.setPlayerBets(Arrays.asList(playerBet1, playerBet2, playerBet3));

        Game game2 = new Game(2L);
        game2.setPlayerBets(Arrays.asList(playerBet2, playerBet3, playerBet3, playerBet4));

        Game game3 = new Game(3L);
        game3.setPlayerBets(Arrays.asList(playerBet1, playerBet2));

        Game game4 = new Game(4L);

        gameDao.saveAll(Arrays.asList(game1, game2, game3, game4));

        MvcResult result = mvc.perform(
                get(String.format("/games/player/%s/bets", TEST_ID))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String stringBody = result.getResponse().getContentAsString();
        List<BetResponse> response = objectMapper.readValue(stringBody, new TypeReference<List<BetResponse>>() {
        });

        assertEquals(2, response.size());

        BetResponse betResponse1 = response.get(0);
        assertEquals(TEST_ID, betResponse1.getGameId());
        assertThat(betResponse1.getBets(), contains(BigDecimal.ZERO));

        BetResponse betResponse2 = response.get(1);
        assertEquals(2, betResponse2.getGameId().longValue());
        assertThat(betResponse2.getBets(), contains(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.TEN));
    }

    @Test
    public void placeBet_ifGameNotExist_thenReturnHttpStatus404() throws Exception {
        PlaceBetRequest placeBetRequest = new PlaceBetRequest();
        placeBetRequest.setPlayerId(TEST_ID);
        placeBetRequest.setAmount(BigDecimal.TEN);

        mvc.perform(
                post(String.format("/game/%s/bet", TEST_ID))
                        .content(asJsonString(placeBetRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(RestApiException.Type.GAME_NOT_FOUND.getMessage())))
                .andExpect(jsonPath("$.code", is(RestApiException.Type.GAME_NOT_FOUND.getCode())));
    }

    @Test
    public void placeBet_ifPlaceBetAmountLtZero_thenReturnHttpStatus400() throws Exception {
        PlaceBetRequest placeBetRequest = new PlaceBetRequest();
        placeBetRequest.setPlayerId(TEST_ID);
        placeBetRequest.setAmount(BigDecimal.TEN.negate());

        mvc.perform(
                post(String.format("/game/%s/bet", TEST_ID))
                        .content(asJsonString(placeBetRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void placeBet_ifGameExist_thenReturnUpdatedBalance() throws Exception {
        gameDao.save(new Game(TEST_ID));

        PlaceBetRequest placeBetRequest = new PlaceBetRequest();
        placeBetRequest.setPlayerId(TEST_ID);
        placeBetRequest.setAmount(BigDecimal.ONE);

        mvc.perform(
                post(String.format("/game/%s/bet", TEST_ID))
                        .content(asJsonString(placeBetRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(RestApiException.Type.GAME_NOT_FOUND.getMessage())))
                .andExpect(jsonPath("$.code", is(RestApiException.Type.GAME_NOT_FOUND.getCode())));
    }

    @After
    public void clean() {
        gameDao.deleteAll();
    }

    private String asJsonString(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    private Long getId() {
        return new Random().nextLong();
    }
}
