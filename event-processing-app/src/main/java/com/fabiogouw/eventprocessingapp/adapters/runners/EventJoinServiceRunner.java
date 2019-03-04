package com.fabiogouw.eventprocessingapp.adapters.runners;

import com.fabiogouw.eventprocessingapp.adapters.dtos.Debit;
import com.fabiogouw.eventprocessingapp.ports.DebitNotifier;
import com.fabiogouw.ports.JoinManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;

@Service
public class EventJoinServiceRunner implements CommandLineRunner {

    private final JoinManager _joiner;
    private final Logger _logger = LoggerFactory.getLogger(EventJoinServiceRunner.class);
    private final DebitNotifier _debitNotifier;

    public EventJoinServiceRunner(@Qualifier("fraudAndLimitJoinForWithdraw") JoinManager joiner,
                                  @Qualifier("fraudAndLimitJoinForWithdraw") DebitNotifier debitNotifier) {
        _joiner = joiner;
        _debitNotifier = debitNotifier;
    }

    @Override
    public void run(String... args) throws Exception {
        _joiner.setBehavior(Arrays.asList("com.fabiogouw.eventprocessingdemo.FraudAnalysisResult",
                "com.fabiogouw.eventprocessingdemo.LimitAnalysisResult"), (join) -> {
            _logger.info("Join completed and emitting a debit request for {}...", join.getId());
            _debitNotifier.notifyDebit(new Debit(UUID.randomUUID(), UUID.fromString(join.getId()), "ZZZ", 100.23d));
        });
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                _joiner.stop();
            }
        });
    }


}
