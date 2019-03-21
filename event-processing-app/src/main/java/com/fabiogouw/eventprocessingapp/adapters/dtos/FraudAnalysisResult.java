package com.fabiogouw.eventprocessingapp.adapters.dtos;

public class FraudAnalysisResult {

    public static final String EVENT_TYPE = "com.fabiogouw.eventprocessingdemo.FraudAnalysisResult";

    private String _correlationId;
    private String _accountFrom;
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

    public String getResult() {
        return _result;
    }

    public void setResult(String value) {
        _result = value;
    }

    public FraudAnalysisResult() {

    }

    public FraudAnalysisResult(String correlationId, String accountFrom, String result) {
        _correlationId = correlationId;
        _accountFrom = accountFrom;
        _result = result;
    }
}
