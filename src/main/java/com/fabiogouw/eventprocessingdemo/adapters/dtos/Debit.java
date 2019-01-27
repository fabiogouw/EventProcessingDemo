package com.fabiogouw.eventprocessingdemo.adapters.dtos;

import java.util.UUID;

public class Debit {
    private UUID _id;
    private UUID _correlationId;
    private String _accountFrom;
    private double _amount;

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

    public Debit() {

    }

    public Debit(UUID id, UUID correlationId, String accountFrom, double amount) {
        _id = id;
        _correlationId = correlationId;
        _accountFrom = accountFrom;
        _amount = amount;
    }
}
