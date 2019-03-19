package com.fabiogouw.eventprocessingapp.adapters.handlers;

import com.fabiogouw.eventprocessingapp.adapters.dtos.LimitAnalysisResult;
import com.fabiogouw.eventprocessingapp.adapters.dtos.Withdraw;
import com.fabiogouw.eventprocessingapp.ports.LimitAnalysisNotifier;
import com.fabiogouw.eventprocessinglib.dtos.CustomEvent;
import com.fabiogouw.eventprocessinglib.ports.EventHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LimitAnalysisEventHandler implements EventHandler {

    private final Logger _logger = LoggerFactory.getLogger(LimitAnalysisEventHandler.class);
    private final LimitAnalysisNotifier _limitAnalysisNotifier;
    private final ObjectMapper _mapper = new ObjectMapper();

    public LimitAnalysisEventHandler(LimitAnalysisNotifier limitAnalysisNotifier) {
        _limitAnalysisNotifier = limitAnalysisNotifier;
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
            _logger.info("Analysing limit for operation '{}'...", event.getCorrelationId());
            String analysisResult = withdraw.getAmount() > 5000 ? "nok" : "ok";
            LimitAnalysisResult result = new LimitAnalysisResult(withdraw.getCorrelationId().toString(), withdraw.getAccountFrom().toString(), withdraw.getAmount(), analysisResult);
            _limitAnalysisNotifier.notifyResult(result);
        }
    }
}
