package com.fabiogouw.eventprocessingdemo.ports;

import com.fabiogouw.eventprocessingdemo.adapters.dtos.Withdraw;

public interface WithdrawNotifier {
    void notifyWithdraw(Withdraw withdraw);
}
