package com.fabiogouw.eventprocessingdemo.ports;

import com.fabiogouw.eventprocessingdemo.adapters.dtos.Debit;

public interface DebitNotifier {
    void notifyDebit(Debit debit);
}
