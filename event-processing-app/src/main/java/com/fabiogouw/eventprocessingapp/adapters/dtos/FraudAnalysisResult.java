package com.fabiogouw.eventprocessingapp.adapters.dtos;

import java.util.UUID;

public class FraudAnalysisResult {
    private UUID _id;
    private UUID _correlationId;
    private String _accountTo;
    private String _result;

    public UUID getId() {
        return _id;
    }

    public void setId(UUID value) {
        _id = value;
    }

    public UUID getCorrelationId() {
        return _correlationId;
    }

    public void setCorrelationId(UUID value) {
        _correlationId = value;
    }

    public String getAccountTo() {
        return _accountTo;
    }

    public void setAccountTo(String value) {
        _accountTo = value;
    }

    public String getResult() {
        return _result;
    }

    public void setResult(String value) {
        _result = value;
    }

    public FraudAnalysisResult() {

    }

    public FraudAnalysisResult(UUID id, UUID correlationId, String accountTo, String result) {
        _id = id;
        _correlationId = correlationId;
        _accountTo = accountTo;
        _result = result;
    }
}
