package com.fabiogouw.eventprocessingapp.adapters.handlers;

import com.fabiogouw.eventprocessingapp.adapters.dtos.Debit;
import com.fabiogouw.eventprocessingapp.adapters.dtos.FraudAnalysisResult;
import com.fabiogouw.eventprocessingapp.adapters.dtos.Withdraw;
import com.fabiogouw.eventprocessingapp.ports.DebitNotifier;
import com.fabiogouw.eventprocessinglib.dtos.CustomEvent;
import com.fabiogouw.eventprocessinglib.ports.EventHandler;
import com.fabiogouw.ports.JoinNotifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class WithdrawFraudAnalysisEventHandler implements EventHandler {

    private final Logger _logger = LoggerFactory.getLogger(WithdrawFraudAnalysisEventHandler.class);
    private final JoinNotifier _joinNotifier;
    private final ObjectMapper _mapper = new ObjectMapper();

    public WithdrawFraudAnalysisEventHandler(JoinNotifier joinNotifier) {
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
        Withdraw withdraw = _mapper.convertValue(event.getPayload(), Withdraw.class);
        if(withdraw != null) {
            String id = withdraw.getId().toString();
            _logger.info("Notifying join 'com.fabiogouw.eventprocessingdemo.FraudAnalysisResult' for {}...", id);
            FraudAnalysisResult result = new FraudAnalysisResult(UUID.randomUUID(), UUID.fromString(id),  withdraw.getAccountFrom(), "ok");
            _joinNotifier.notify(id, "com.fabiogouw.eventprocessingdemo.FraudAnalysisResult", result);
        }
    }
}
