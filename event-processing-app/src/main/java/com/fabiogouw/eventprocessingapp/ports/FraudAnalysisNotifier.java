package com.fabiogouw.eventprocessingapp.ports;

import com.fabiogouw.eventprocessingapp.adapters.dtos.FraudAnalysisResult;

public interface FraudAnalysisNotifier {
    void notifyResult(FraudAnalysisResult result);
}
