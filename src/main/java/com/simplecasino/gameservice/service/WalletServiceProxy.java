package com.simplecasino.gameservice.service;


import com.simplecasino.gameservice.dto.BalanceResponse;
import com.simplecasino.gameservice.dto.RegisterPlayerRequest;
import com.simplecasino.gameservice.dto.UpdateBalanceRequest;
import feign.Headers;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "wallet-service")
@RibbonClient(name = "wallet-service")
public interface WalletServiceProxy {

    /*@PostMapping("/player")
    @Headers("Content-Type: application/json")
    BalanceResponse registerPlayer(@RequestBody RegisterPlayerRequest registerRequest);*/

    @PutMapping("/player/{id}/balance")
    @Headers("Content-Type: application/json")
    BalanceResponse updateBalance(@PathVariable Long id, @RequestBody UpdateBalanceRequest updateBalanceRequest);

    /*@GetMapping("/player/{id}/balance")
    BalanceResponse getBalance(@PathVariable Long id);*/
}
