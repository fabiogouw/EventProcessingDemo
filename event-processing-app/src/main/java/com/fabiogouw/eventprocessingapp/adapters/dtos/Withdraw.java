package com.fabiogouw.eventprocessingapp.adapters.dtos;

import java.util.UUID;

public class Withdraw {
    private String _id;
    private String _correlationId;
    private String _accountFrom;
    private double _amount;

    public String getId() {
        return _id;
    }

    public void setId(String value) {
        _id = value;
    }

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

    public Withdraw() {

    }

    public Withdraw(String id, String correlationId, String accountFrom, double amount) {
        _id = id;
        _correlationId = correlationId;
        _accountFrom = accountFrom;
        _amount = amount;
    }
}
