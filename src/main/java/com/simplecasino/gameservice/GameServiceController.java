package com.simplecasino.gameservice;

import com.simplecasino.gameservice.dto.AddGameRequest;
import com.simplecasino.gameservice.dto.BalanceResponse;
import com.simplecasino.gameservice.dto.BetResponse;
import com.simplecasino.gameservice.dto.PlaceBetRequest;
import com.simplecasino.gameservice.model.Game;
import com.simplecasino.gameservice.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class GameServiceController {

    private GameService gameService;

    @Autowired
    public GameServiceController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/game/{id}/bet")
    public BalanceResponse placeBet(@PathVariable Long id, @RequestBody PlaceBetRequest placeBetRequest) {
        return gameService.placeBet(id, placeBetRequest);
    }

    @GetMapping("/games/player{id}/bet")
    public List<BetResponse> placeBet(@PathVariable Long id) {
        return gameService.getPlayerBets(id);
    }

    @PostMapping("/game")
    public void addGame(@RequestBody AddGameRequest addGameRequest) {
        Game game = new Game(addGameRequest.getGameId());
        gameService.saveGame(game);
    }
}
