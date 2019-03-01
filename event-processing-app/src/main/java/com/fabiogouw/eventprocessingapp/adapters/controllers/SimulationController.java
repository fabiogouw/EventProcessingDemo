package com.fabiogouw.eventprocessingapp.adapters.controllers;

import com.fabiogouw.eventprocessingapp.adapters.dtos.Transfer;
import com.fabiogouw.eventprocessingapp.adapters.dtos.Withdraw;
import com.fabiogouw.eventprocessingapp.ports.TransferNotifier;
import com.fabiogouw.eventprocessingapp.ports.WithdrawNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(value = "/simulation")
public class SimulationController {

    private final Logger _logger = LoggerFactory.getLogger(SimulationController.class);

    private final TransferNotifier _transferNotifier;
    private final WithdrawNotifier _withdrawNotifier;

    @Autowired
    SimulationController(TransferNotifier transferNotifier, WithdrawNotifier withdrawNotifier) {
        _transferNotifier = transferNotifier;
        _withdrawNotifier = withdrawNotifier;
    }

    @PostMapping(value = "/test")
    public void sendMessageToKafkaTopic(@RequestParam("count") int count) {
        for(int i = 0; i < count; i++) {
            _transferNotifier.notifyTransfer(new Transfer(UUID.randomUUID(), "AAA", "BBB", 10));
            _withdrawNotifier.notifyWithdraw(new Withdraw(UUID.randomUUID(), "CCC", 20));
        }
    }
}
