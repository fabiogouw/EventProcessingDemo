package com.fabiogouw.eventprocessingapp.adapters.handlers;

import com.fabiogouw.eventprocessingapp.adapters.dtos.FraudAnalysisResult;
import com.fabiogouw.eventprocessingapp.adapters.dtos.Withdraw;
import com.fabiogouw.eventprocessingapp.ports.FraudAnalysisNotifier;
import com.fabiogouw.eventprocessinglib.dtos.CustomEvent;
import com.fabiogouw.eventprocessinglib.ports.EventHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class FraudAnalysisEventHandler implements EventHandler {

    private final Logger _logger = LoggerFactory.getLogger(FraudAnalysisEventHandler.class);
    private final FraudAnalysisNotifier _fraudAnalysisNotifier;
    private final ObjectMapper _mapper = new ObjectMapper();

    public FraudAnalysisEventHandler(FraudAnalysisNotifier fraudAnalysisNotifier) {
        _fraudAnalysisNotifier = fraudAnalysisNotifier;
    }

    @Override
    public String getType() {
        return Withdraw.EVENT_TYPE;
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
            _logger.info("Analysing fraud for operation '{}'...", event.getCorrelationId());
            // simulate some analysis (10% are fraud)
            String analysisResult = UUID.randomUUID().hashCode() % 10 == 0 ? "nok" : "ok";
            FraudAnalysisResult result = new FraudAnalysisResult(event.getCorrelationId(), withdraw.getAccountFrom(), analysisResult);
            _fraudAnalysisNotifier.notifyResult(result);
        }
    }
}
