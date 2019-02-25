package com.fabiogouw.eventprocessingapp.adapters.producers;

import com.fabiogouw.eventprocessingapp.adapters.dtos.Debit;
import com.fabiogouw.eventprocessingapp.ports.DebitNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebitNotifierImpl implements DebitNotifier {

    private static final Logger _logger = LoggerFactory.getLogger(DebitNotifierImpl.class);

    @Override
    public void notifyDebit(Debit debit) {
        _logger.info(String.format("#### -> Producing debit -> %s", debit));
    }
}
