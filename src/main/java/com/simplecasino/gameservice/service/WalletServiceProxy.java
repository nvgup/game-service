package com.simplecasino.gameservice.service;


import com.simplecasino.gameservice.dto.BalanceResponse;
import com.simplecasino.gameservice.dto.UpdateBalanceRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "wallet-service")
public interface WalletServiceProxy {

    @PutMapping("/player/{id}/balance")
    BalanceResponse updateBalance(@PathVariable("id") Long id, @RequestBody UpdateBalanceRequest updateBalanceRequest);
}