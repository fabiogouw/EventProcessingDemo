package com.fabiogouw.eventprocessingapp.adapters.handlers;

import com.fabiogouw.eventprocessingapp.adapters.dtos.Debit;
import com.fabiogouw.eventprocessingapp.adapters.dtos.Withdraw;
import com.fabiogouw.eventprocessingapp.ports.DebitNotifier;
import com.fabiogouw.eventprocessinglib.dtos.CustomEvent;
import com.fabiogouw.eventprocessinglib.ports.EventHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class WithdrawEventHandler implements EventHandler {

    private final Logger _logger = LoggerFactory.getLogger(WithdrawEventHandler.class);
    private final DebitNotifier _debitNotifier;

    public WithdrawEventHandler(DebitNotifier debitNotifier) {
        _debitNotifier = debitNotifier;
    }

    @Override
    public String getType() {
        return "com.fabiogouw.eventprocessingdemo.WithdrawRequested";
    }

    @Override
    public int getLowestVersion() {
        return 1;
    }

    @Override
    public int getHighestVersion() {
        return 1;
    }

    @Override
    public void handle(CustomEvent event) {
        ObjectMapper mapper = new ObjectMapper();
        Withdraw withdraw = mapper.convertValue(event.getPayload(), Withdraw.class);
        if(withdraw != null) {
            _logger.info("Processando um saque e incluindo um novo débito...");
            _debitNotifier.notifyDebit(new Debit(UUID.randomUUID(), withdraw.getId(), withdraw.getAccountFrom(), withdraw.getAmount()));
        }
    }
}