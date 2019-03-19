package com.fabiogouw.eventprocessingapp.adapters.producers;

import com.fabiogouw.eventprocessingapp.adapters.dtos.Withdraw;
import com.fabiogouw.eventprocessingapp.ports.WithdrawNotifier;
import com.fabiogouw.eventprocessinglib.dtos.CustomEvent;
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
        Message<Withdraw> message = MessageBuilder
                .withPayload(withdraw)
                .setHeader(KafkaHeaders.TOPIC, TOPIC)
                .setHeader(KafkaHeaders.MESSAGE_KEY, withdraw.getCorrelationId())
                .setHeader(CustomEvent.CORRELATION_ID, withdraw.getCorrelationId())
                .setHeader(CustomEvent.EVENT_TYPE, "com.fabiogouw.eventprocessingdemo.WithdrawRequested")
                .setHeader(CustomEvent.EVENT_TYPE_VERSION, 1)
                .build();
        _logger.info(String.format("#### -> Producing message -> %s", message));
        _kafkaTemplate.send(message);
    }
}
