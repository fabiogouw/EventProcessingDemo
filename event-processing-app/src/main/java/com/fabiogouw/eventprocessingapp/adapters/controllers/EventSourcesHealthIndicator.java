package com.fabiogouw.eventprocessingapp.adapters.controllers;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class EventSourcesHealthIndicator extends AbstractHealthIndicator {

    private final KafkaListenerEndpointRegistry _kafkaListenerEndpointRegistry;

    public EventSourcesHealthIndicator(KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry) {
        _kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        Collection<MessageListenerContainer> containers = _kafkaListenerEndpointRegistry.getListenerContainers();
        boolean isOk = true;
        for(MessageListenerContainer container : containers) {
            if(!container.isRunning()) {
                isOk = false;
            }
        }
        if(isOk) {
            builder.up();
        }
        else {
            builder.down();
        }
    }
}
