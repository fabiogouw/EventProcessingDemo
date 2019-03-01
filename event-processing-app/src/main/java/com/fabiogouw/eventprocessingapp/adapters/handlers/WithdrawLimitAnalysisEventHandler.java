package com.fabiogouw.eventprocessingapp.adapters.handlers;

import com.fabiogouw.eventprocessingapp.adapters.dtos.Withdraw;
import com.fabiogouw.eventprocessinglib.dtos.CustomEvent;
import com.fabiogouw.eventprocessinglib.ports.EventHandler;
import com.fabiogouw.ports.JoinNotifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WithdrawLimitAnalysisEventHandler implements EventHandler {

    private final Logger _logger = LoggerFactory.getLogger(WithdrawLimitAnalysisEventHandler.class);
    private final JoinNotifier _joinNotifier;

    public WithdrawLimitAnalysisEventHandler(JoinNotifier joinNotifier) {
        _joinNotifier = joinNotifier;
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
            String id = withdraw.getId().toString();
            _logger.info("Notifying join 'com.fabiogouw.eventprocessingdemo.LimitAnalysisResult' for {}...", id);
            _joinNotifier.notify(id, "com.fabiogouw.eventprocessingdemo.LimitAnalysisResult");
        }
    }
}
