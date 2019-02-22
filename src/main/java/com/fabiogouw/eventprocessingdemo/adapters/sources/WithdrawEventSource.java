package com.fabiogouw.eventprocessingdemo.adapters.sources;

import com.fabiogouw.eventprocessingdemo.adapters.dtos.CustomEvent;
import com.fabiogouw.eventprocessingdemo.adapters.services.EventConsumer;
import com.fabiogouw.eventprocessingdemo.ports.EventSource;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.Acknowledgment;

import java.util.function.Consumer;

public class WithdrawEventSource implements EventSource {

    private final Logger _logger = LoggerFactory.getLogger(WithdrawEventSource.class);
    private static final String LISTENER_ID = "WithdrawEventSource";
    private final KafkaListenerEndpointRegistry _registry;
    private Consumer<CustomEvent> _run;

    public WithdrawEventSource(KafkaListenerEndpointRegistry registry) {
        _registry = registry;
    }

    @KafkaListener(id = LISTENER_ID, topics = {"transfers", "withdraws"}, containerFactory = "containerFactory")
    public void listen(ConsumerRecord<String, CustomEvent> message, Acknowledgment acknowledgment) {
        _logger.info("#### -> Consumed message -> '{}' : '{}' / '{}'", message.topic(),  message.partition(), message.offset());
        CustomEvent event = message.value();
        _run.accept(event);
        acknowledgment.acknowledge();
    }

    @Override
    public void setProcessor(Consumer<CustomEvent> run) {
        _run = run;
        _registry.getListenerContainer(LISTENER_ID).start();
    }
}
