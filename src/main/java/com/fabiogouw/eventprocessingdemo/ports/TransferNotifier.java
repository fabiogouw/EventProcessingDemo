package com.fabiogouw.eventprocessingdemo.ports;

import com.fabiogouw.eventprocessingdemo.adapters.dtos.Transfer;

public interface TransferNotifier {
    void notifyTransfer(Transfer transfer);
}
