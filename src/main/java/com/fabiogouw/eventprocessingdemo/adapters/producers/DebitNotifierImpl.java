package com.fabiogouw.eventprocessingdemo.adapters.producers;

import com.fabiogouw.eventprocessingdemo.adapters.dtos.Debit;
import com.fabiogouw.eventprocessingdemo.ports.DebitNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

public class DebitNotifierImpl implements DebitNotifier {

    private static final Logger _logger = LoggerFactory.getLogger(DebitNotifierImpl.class);

    @Override
    public void notifyDebit(Debit debit) {
        _logger.info(String.format("#### -> Producing debit -> %s", debit));
    }
}
