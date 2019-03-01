package com.fabiogouw.eventprocessingapp.adapters.dtos;

import java.util.UUID;

public class LimitAnalysisResult {
    private UUID _id;
    private UUID _correlationId;
    private String _accountFrom;
    private double _amount;
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

    public double getAmount() {
        return _amount;
    }

    public void setAmount(double value) {
        _amount = value;
    }

    public String getResult() {
        return _result;
    }

    public void setResult(String value) {
        _result = value;
    }

    public LimitAnalysisResult() {

    }

    public LimitAnalysisResult(UUID id, UUID correlationId, String accountFrom, double amount, String result) {
        _id = id;
        _correlationId = correlationId;
        _accountFrom = accountFrom;
        _amount = amount;
        _result = result;
    }
}
