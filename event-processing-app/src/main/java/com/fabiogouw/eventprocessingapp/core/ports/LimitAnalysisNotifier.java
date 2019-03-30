package com.fabiogouw.eventprocessingapp.core.ports;

import com.fabiogouw.eventprocessingapp.core.dtos.LimitAnalysisResult;

public interface LimitAnalysisNotifier {
    void notifyResult(LimitAnalysisResult result);
}
