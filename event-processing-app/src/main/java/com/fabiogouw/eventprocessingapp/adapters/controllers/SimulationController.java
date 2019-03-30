package com.fabiogouw.eventprocessingapp.adapters.controllers;

import com.fabiogouw.eventprocessingapp.core.dtos.Withdraw;
import com.fabiogouw.eventprocessingapp.core.ports.WithdrawNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping(value = "/simulation")
public class SimulationController {

    private final Logger _logger = LoggerFactory.getLogger(SimulationController.class);

    private final WithdrawNotifier _withdrawNotifier;

    public SimulationController(WithdrawNotifier withdrawNotifier) {
        _withdrawNotifier = withdrawNotifier;
    }

    @PostMapping(value = "/test")
    public void sendMessageToKafkaTopic(@RequestParam("count") int count) {
        Random rnd = new Random();
        for(int i = 0; i < count; i++) {

            Withdraw withdraw = new Withdraw(UUID.randomUUID().toString(), "", rnd.nextInt(5999) + 1);
            _withdrawNotifier.notifyWithdraw(withdraw);
        }
    }
}
