package com.fabiogouw.adapters;

import com.fabiogouw.domain.State;
import com.fabiogouw.ports.RewindableEventSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class KafkaRewindableEventSource implements RewindableEventSource {

    private final Logger _logger = LoggerFactory.getLogger(KafkaRewindableEventSource.class);

    @Override
    public void subscribe(Consumer<State> run) {
        _logger.info("KafkaRewindableEventSource subscribed.");
        run.accept(new State("1", "Able", 0, 0));
        run.accept(new State("1", "Baker", 0, 1));
        run.accept(new State("1", "Charlie", 0, 2));
    }

    @Override
    public void rewindTo(long offset) {
        _logger.info("KafkaRewindableEventSource rewind to {}.", offset);
    }

    @Override
    public void unsubscribe() {
        _logger.info("KafkaRewindableEventSource unsubscribed.");
    }
}
