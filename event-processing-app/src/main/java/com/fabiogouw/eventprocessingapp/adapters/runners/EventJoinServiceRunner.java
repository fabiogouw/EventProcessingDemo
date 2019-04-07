package com.fabiogouw.eventprocessingapp.adapters.runners;

import com.fabiogouw.eventprocessingapp.core.dtos.Debit;
import com.fabiogouw.eventprocessingapp.core.ports.DebitNotifier;
import com.fabiogouw.domain.ports.JoinManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
public class EventJoinServiceRunner implements CommandLineRunner {

    private final JoinManager _joinManager;
    private final Logger _logger = LoggerFactory.getLogger(EventJoinServiceRunner.class);
    private final DebitNotifier _debitNotifier;

    public EventJoinServiceRunner(@Qualifier("fraudAndLimitJoinForWithdraw") JoinManager joinManager,
                                  DebitNotifier debitNotifier) {
        _joinManager = joinManager;
        _debitNotifier = debitNotifier;
    }

    @Override
    public void run(String... args) throws Exception {
        _joinManager.start();
        /*_joinManager.setBehavior((join) -> {
            _logger.info("Join completed and emitting a debit request for {}...", join.getId());
            _debitNotifier.notifyDebit(new Debit(join.getId(), "ZZZ", 100.23d));
        });*/
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                _joinManager.stop();
            }
        });
    }


}
