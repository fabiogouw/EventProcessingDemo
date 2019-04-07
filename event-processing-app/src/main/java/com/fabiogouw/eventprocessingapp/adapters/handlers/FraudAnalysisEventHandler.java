package com.fabiogouw.eventprocessingapp.adapters.handlers;

import com.fabiogouw.domain.ports.JoinNotifier;
import com.fabiogouw.eventprocessingapp.core.dtos.FraudAnalysisResult;
import com.fabiogouw.eventprocessingapp.core.dtos.Withdraw;
import com.fabiogouw.eventprocessingapp.core.ports.FraudAnalysisNotifier;
import com.fabiogouw.eventprocessinglib.core.dtos.CustomEvent;
import com.fabiogouw.eventprocessinglib.core.ports.EventHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.UUID;

public class FraudAnalysisEventHandler implements EventHandler {

    private final Logger _logger = LoggerFactory.getLogger(FraudAnalysisEventHandler.class);
    private final FraudAnalysisNotifier _fraudAnalysisNotifier;
    private final JoinNotifier _joinNotifier;
    private final ObjectMapper _mapper;
    private final Random _rnd = new Random();

    public FraudAnalysisEventHandler(FraudAnalysisNotifier fraudAnalysisNotifier,
                                     JoinNotifier joinNotifier,
                                     ObjectMapper mapper) {
        _fraudAnalysisNotifier = fraudAnalysisNotifier;
        _joinNotifier = joinNotifier;
        _mapper = mapper;
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
            _logger.info("Analysing fraud for operation '{}'...", event.getCorrelationId());
            try {
                Thread.sleep(_rnd.nextInt(100));    // simulating some delay in the process
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // simulate some analysis (10% are fraud)
            String analysisResult = UUID.randomUUID().hashCode() % 10 == 0 ? "nok" : "ok";
            FraudAnalysisResult result = new FraudAnalysisResult(event.getCorrelationId(), withdraw.getAccountFrom(), analysisResult);
            _fraudAnalysisNotifier.notifyResult(result);
            _joinNotifier.notify(event.getCorrelationId(), "FRAUD_OK");
        }
    }
}
