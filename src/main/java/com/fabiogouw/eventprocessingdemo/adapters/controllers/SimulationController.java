package com.fabiogouw.eventprocessingdemo.adapters.controllers;

import com.fabiogouw.eventprocessingdemo.adapters.dtos.Transfer;
import com.fabiogouw.eventprocessingdemo.adapters.dtos.Withdraw;
import com.fabiogouw.eventprocessingdemo.ports.TransferNotifier;
import com.fabiogouw.eventprocessingdemo.ports.WithdrawNotifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(value = "/simulation")
public class SimulationController {

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
