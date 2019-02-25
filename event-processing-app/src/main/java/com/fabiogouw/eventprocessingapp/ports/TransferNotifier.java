package com.fabiogouw.eventprocessingapp.ports;

import com.fabiogouw.eventprocessingapp.adapters.dtos.Transfer;

public interface TransferNotifier {
    void notifyTransfer(Transfer transfer);
}
