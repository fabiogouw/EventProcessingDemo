package com.fabiogouw.eventprocessingdemo.adapters.producers;

import com.fabiogouw.eventprocessingdemo.adapters.dtos.CustomEvent;
import com.fabiogouw.eventprocessingdemo.adapters.dtos.Withdraw;
import com.fabiogouw.eventprocessingdemo.ports.WithdrawNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

public class WithdrawNotifierImpl implements WithdrawNotifier {

    private static final Logger _logger = LoggerFactory.getLogger(WithdrawNotifierImpl.class);
    private static final String TOPIC = "withdraws";

    private KafkaTemplate<String, Withdraw> _kafkaTemplate;

    public WithdrawNotifierImpl(KafkaTemplate<String, Withdraw> kafkaTemplate) {
        _kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void notifyWithdraw(Withdraw withdraw) {
        Message<CustomEvent> message = MessageBuilder
                .withPayload(new CustomEvent("com.fabiogouw.eventprocessingdemo.WithdrawRequested", 1, withdraw))
                .setHeader(KafkaHeaders.TOPIC, TOPIC)
                .setHeader(KafkaHeaders.MESSAGE_KEY, withdraw.getId().toString())
                .setHeader("event_type", "com.fabiogouw.eventprocessingdemo.WithdrawRequested")
                .build();
        _logger.info(String.format("#### -> Producing message -> %s", message));
        _kafkaTemplate.send(message);
    }
}
