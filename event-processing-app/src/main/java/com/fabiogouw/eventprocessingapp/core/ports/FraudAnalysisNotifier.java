package com.fabiogouw.eventprocessingapp.core.ports;

import com.fabiogouw.eventprocessingapp.core.dtos.FraudAnalysisResult;

public interface FraudAnalysisNotifier {
    void notifyResult(FraudAnalysisResult result);
}
