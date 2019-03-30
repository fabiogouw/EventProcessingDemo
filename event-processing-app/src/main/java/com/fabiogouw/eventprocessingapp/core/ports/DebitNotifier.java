package com.fabiogouw.eventprocessingapp.core.ports;

import com.fabiogouw.eventprocessingapp.core.dtos.Debit;

public interface DebitNotifier {
    void notifyDebit(Debit debit);
}
