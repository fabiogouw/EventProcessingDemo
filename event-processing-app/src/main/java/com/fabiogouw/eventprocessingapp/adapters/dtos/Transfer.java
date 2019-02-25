package com.fabiogouw.eventprocessingapp.adapters.dtos;

import java.util.UUID;

public class Transfer {
    private UUID _id;
    private String _accountFrom;
    private String _accountTo;
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

    public String getAccountTo() {
        return _accountTo;
    }

    public void setAccountTo(String value) {
        _accountTo = value;
    }

    public double getAmount() {
        return _amount;
    }

    public void setAmount(double value) {
        _amount = value;
    }

    public Transfer() {

    }

    public Transfer(UUID id, String accountFrom, String accountTo, double amount) {
        _id = id;
        _accountFrom = accountFrom;
        _accountTo = accountTo;
        _amount = amount;
    }
}
