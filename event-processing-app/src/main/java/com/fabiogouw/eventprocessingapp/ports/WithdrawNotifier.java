package com.fabiogouw.eventprocessingapp.ports;

import com.fabiogouw.eventprocessingapp.adapters.dtos.Withdraw;

public interface WithdrawNotifier {
    void notifyWithdraw(Withdraw withdraw);
}
