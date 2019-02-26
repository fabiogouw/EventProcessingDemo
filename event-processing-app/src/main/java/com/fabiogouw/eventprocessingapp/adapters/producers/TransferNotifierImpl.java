package com.fabiogouw.eventprocessingapp.adapters.producers;

import com.fabiogouw.eventprocessingapp.adapters.dtos.Transfer;
import com.fabiogouw.eventprocessingapp.ports.TransferNotifier;
import com.fabiogouw.eventprocessinglib.dtos.CustomEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

public class TransferNotifierImpl implements TransferNotifier {

    private static final Logger _logger = LoggerFactory.getLogger(TransferNotifierImpl.class);
    private static final String TOPIC = "transfers";

    private KafkaTemplate<String, Transfer> _kafkaTemplate;

    public TransferNotifierImpl(KafkaTemplate<String, Transfer> kafkaTemplate) {
        _kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void notifyTransfer(Transfer transfer) {
        Message<CustomEvent> message = MessageBuilder
                .withPayload(new CustomEvent("com.fabiogouw.eventprocessingdemo.TransferRequested", 1, transfer))
                .setHeader(KafkaHeaders.TOPIC, TOPIC)
                .setHeader(KafkaHeaders.MESSAGE_KEY, transfer.getId().toString())
                .setHeader("event_type", "com.fabiogouw.eventprocessingdemo.TransferRequested")
                .build();
        _logger.info(String.format("#### -> Producing message -> %s", message));
        _kafkaTemplate.send(message);
    }
}