package com.fabiogouw.eventprocessingapp.core.dtos;

public class Debit {
    private String _correlationId;
    private String _accountFrom;
    private double _amount;

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

    public Debit() {

    }

    public Debit(String correlationId, String accountFrom, double amount) {
        _correlationId = correlationId;
        _accountFrom = accountFrom;
        _amount = amount;
    }
}
