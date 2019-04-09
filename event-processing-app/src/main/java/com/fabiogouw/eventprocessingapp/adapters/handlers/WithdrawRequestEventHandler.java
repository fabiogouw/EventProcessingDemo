package com.fabiogouw.eventprocessingapp.adapters.handlers;

import com.fabiogouw.domain.ports.ReactiveStateMachineEventNotifier;
import com.fabiogouw.eventprocessingapp.core.dtos.Withdraw;
import com.fabiogouw.eventprocessingapp.core.ports.WithdrawNotifier;
import com.fabiogouw.eventprocessinglib.core.dtos.CustomEvent;
import com.fabiogouw.eventprocessinglib.core.ports.EventHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WithdrawRequestEventHandler implements EventHandler {

    private final Logger _logger = LoggerFactory.getLogger(WithdrawRequestEventHandler.class);
    private final WithdrawNotifier _withdrawNotifier;
    private final ReactiveStateMachineEventNotifier _reactiveStateMachineEventNotifier;

    private final ObjectMapper _mapper;

    public WithdrawRequestEventHandler(WithdrawNotifier withdrawNotifier,
                                       ReactiveStateMachineEventNotifier reactiveStateMachineEventNotifier,
                                       ObjectMapper mapper) {
        _withdrawNotifier = withdrawNotifier;
        _reactiveStateMachineEventNotifier = reactiveStateMachineEventNotifier;
        _mapper = mapper;
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
            _reactiveStateMachineEventNotifier.notify(event.getCorrelationId(), "VALIDATE");
            _withdrawNotifier.notifyWithdraw(withdraw);
        }
        else {
            _logger.warn("Invalid withdraw: {}", withdraw);
        }
    }
}
