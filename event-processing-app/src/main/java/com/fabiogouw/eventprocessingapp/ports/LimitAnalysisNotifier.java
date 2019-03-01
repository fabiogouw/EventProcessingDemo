package com.fabiogouw.eventprocessingapp.ports;

import com.fabiogouw.eventprocessingapp.adapters.dtos.LimitAnalysisResult;

public interface LimitAnalysisNotifier {
    void notifyResult(LimitAnalysisResult result);
}
