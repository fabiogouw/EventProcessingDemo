package com.fabiogouw.eventprocessingapp.core.ports;

import com.fabiogouw.eventprocessingapp.core.dtos.Withdraw;

public interface WithdrawNotifier {
    void notifyWithdraw(Withdraw withdraw);
}
