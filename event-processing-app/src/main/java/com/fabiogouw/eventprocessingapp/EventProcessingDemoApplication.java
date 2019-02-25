package com.fabiogouw.eventprocessingapp;

import com.fabiogouw.eventprocessinglib.adapters.services.EventConsumerImpl;
import com.fabiogouw.eventprocessinglib.ports.EventConsumer;
import com.fabiogouw.eventprocessinglib.ports.EventHandler;
import com.fabiogouw.eventprocessinglib.ports.EventSource;
import io.micrometer.core.instrument.Timer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class EventProcessingDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventProcessingDemoApplication.class, args);
    }

    @Bean
    public EventConsumer getEventConsumer(List<EventHandler> handlers, List<EventSource> sources, Timer timer) {
        return new EventConsumerImpl(handlers, sources, timer);
    }
}
