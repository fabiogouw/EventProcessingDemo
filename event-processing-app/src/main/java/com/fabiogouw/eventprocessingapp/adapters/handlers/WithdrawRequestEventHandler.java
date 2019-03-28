package com.fabiogouw.eventprocessingapp.adapters.handlers;

import com.fabiogouw.eventprocessingapp.adapters.dtos.Withdraw;
import com.fabiogouw.eventprocessingapp.ports.Holder;
import com.fabiogouw.eventprocessingapp.ports.WithdrawNotifier;
import com.fabiogouw.eventprocessinglib.dtos.CustomEvent;
import com.fabiogouw.eventprocessinglib.ports.EventHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.UUID;

public class WithdrawRequestEventHandler implements EventHandler {

    private final Logger _logger = LoggerFactory.getLogger(WithdrawRequestEventHandler.class);
    private final WithdrawNotifier _withdrawNotifier;
    private final Holder<Withdraw> _holder;
    private final ObjectMapper _mapper = new ObjectMapper();

    public WithdrawRequestEventHandler(WithdrawNotifier withdrawNotifier, Holder<Withdraw> holder) {
        _withdrawNotifier = withdrawNotifier;
        _holder = holder;
    }

    @Override
    public String getType() {
        return "com.fabiogouw.eventprocessingdemo.WithdrawRequest";
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
        Withdraw withdraw = _mapper.convertValue(event.getPayload(), Withdraw.class);
        if(withdraw != null && withdraw.getAmount() > 0) {
            _logger.info("Producing withdraw '{}'...", event.getCorrelationId());
            _withdrawNotifier.notifyWithdraw(withdraw);
            _holder.release(withdraw.getCorrelationId(), withdraw);
        }
        else {
            _logger.warn("Invalid withdraw: {}", withdraw);
        }
    }
}
