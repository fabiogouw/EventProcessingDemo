package com.fabiogouw.eventprocessingapp.adapters.controllers;

import com.fabiogouw.eventprocessingapp.adapters.dtos.Withdraw;
import com.fabiogouw.eventprocessingapp.adapters.sources.WebEventSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping(value = "/withdraw")
public class WithdrawController {
    private final Logger _logger = LoggerFactory.getLogger(WithdrawController.class);

    private final WebEventSource<Withdraw> _withdrawEventSource;

    public WithdrawController(WebEventSource<Withdraw> withdrawEventSource) {
        _withdrawEventSource = withdrawEventSource;
    }

    @PostMapping(value = "/test")
    public Withdraw sendMessageToKafkaTopic(@RequestParam("count") int count) {
        Random rnd = new Random();
        Withdraw withdraw = new Withdraw(UUID.randomUUID().toString(), "AAA", rnd.nextInt(5999) + 1);
        _withdrawEventSource.send(withdraw.getCorrelationId(), "com.fabiogouw.eventprocessingdemo.WithdrawRequested", 1, withdraw);
        return withdraw;
    }
}
