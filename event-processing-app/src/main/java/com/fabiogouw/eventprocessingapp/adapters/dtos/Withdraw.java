package com.fabiogouw.eventprocessingapp.adapters.dtos;

import java.util.UUID;

public class Withdraw {
    private UUID _id;
    private String _accountFrom;
    private double _amount;

    public UUID getId() {
        return _id;
    }

    public void setId(UUID value) {
        _id = value;
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

    public Withdraw(UUID id, String accountFrom, double amount) {
        _id = id;
        _accountFrom = accountFrom;
        _amount = amount;
    }
}
