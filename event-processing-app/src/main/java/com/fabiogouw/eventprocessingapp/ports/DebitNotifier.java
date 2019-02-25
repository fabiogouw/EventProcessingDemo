package com.fabiogouw.eventprocessingapp.ports;

import com.fabiogouw.eventprocessingapp.adapters.dtos.Debit;

public interface DebitNotifier {
    void notifyDebit(Debit debit);
}
