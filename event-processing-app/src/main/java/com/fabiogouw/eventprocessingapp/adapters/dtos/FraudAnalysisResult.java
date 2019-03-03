package com.fabiogouw.eventprocessingapp.adapters.dtos;

import java.util.UUID;

public class FraudAnalysisResult {
    private UUID _id;
    private UUID _correlationId;
    private String _accountFrom;
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

    public String getAccountFrom() {
        return _accountFrom;
    }

    public void setAccountFrom(String value) {
        _accountFrom = value;
    }

    public String getResult() {
        return _result;
    }

    public void setResult(String value) {
        _result = value;
    }

    public FraudAnalysisResult() {

    }

    public FraudAnalysisResult(UUID id, UUID correlationId, String accountFrom, String result) {
        _id = id;
        _correlationId = correlationId;
        _accountFrom = accountFrom;
        _result = result;
    }
}
