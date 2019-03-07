package com.fabiogouw.eventprocessingapp.adapters.dtos;

public class LimitAnalysisResult {

    public static final String EVENT_TYPE = "com.fabiogouw.eventprocessingdemo.LimitAnalysisResult";

    private String _correlationId;
    private String _accountFrom;
    private double _amount;
    private String _result;

    public String getCorrelationId() {
        return _correlationId;
    }

    public void setCorrelationId(String value) {
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

    public LimitAnalysisResult(String correlationId, String accountFrom, double amount, String result) {
        _correlationId = correlationId;
        _accountFrom = accountFrom;
        _amount = amount;
        _result = result;
    }
}
