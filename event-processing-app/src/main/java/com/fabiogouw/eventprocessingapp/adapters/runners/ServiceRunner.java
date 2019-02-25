package com.fabiogouw.eventprocessingapp.adapters.runners;

import com.fabiogouw.eventprocessinglib.ports.EventConsumer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
public class ServiceRunner implements CommandLineRunner {

    private final EventConsumer _consumer;

    public ServiceRunner(EventConsumer consumer) {
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
