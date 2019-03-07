package com.fabiogouw.eventprocessingapp.adapters.sources;

import com.fabiogouw.eventprocessinglib.dtos.CustomEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.Acknowledgment;

public class WithdrawEventSource extends SpringKafkaEventSource {

    private final Logger _logger = LoggerFactory.getLogger(WithdrawEventSource.class);
    private static final String LISTENER_ID = "WithdrawEventSource";

    public WithdrawEventSource(KafkaListenerEndpointRegistry registry) {
        super(registry);
    }

    @Override
    public String getListenerId() {
        return LISTENER_ID;
    }

    @KafkaListener(id = LISTENER_ID, topics = {"withdraws"}, containerFactory = "containerFactory")
    public void listen(ConsumerRecord<String, CustomEvent> message, Acknowledgment acknowledgment) {
        _logger.info("#### -> Consumed message -> '{}' : '{}' / '{}'", message.topic(),  message.partition(), message.offset());
        CustomEvent event = message.value();
        run(event);
        acknowledgment.acknowledge();
    }
}
