package com.fabiogouw.eventprocessingapp.adapters.runners;

import com.fabiogouw.eventprocessinglib.ports.EventConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
public class EventConsumerServiceRunner implements CommandLineRunner {

    private final EventConsumer _consumer;
    private final Logger _logger = LoggerFactory.getLogger(EventConsumerServiceRunner.class);

    public EventConsumerServiceRunner(EventConsumer consumer) {
        _consumer = consumer;
    }

    @Override
    public void run(String... args) throws Exception {
        _consumer.start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                _consumer.stop();
            }
        });
    }


}
