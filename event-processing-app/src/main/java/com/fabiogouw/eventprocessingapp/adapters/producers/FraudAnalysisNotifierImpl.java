package com.fabiogouw.eventprocessingapp.adapters.producers;

import com.fabiogouw.eventprocessingapp.adapters.dtos.FraudAnalysisResult;
import com.fabiogouw.eventprocessingapp.adapters.dtos.Withdraw;
import com.fabiogouw.eventprocessingapp.ports.FraudAnalysisNotifier;
import com.fabiogouw.eventprocessinglib.dtos.CustomEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

public class FraudAnalysisNotifierImpl implements FraudAnalysisNotifier {

    private static final Logger _logger = LoggerFactory.getLogger(FraudAnalysisNotifierImpl.class);
    private static final String TOPIC = "withdraws";

    private KafkaTemplate<String, FraudAnalysisResult> _kafkaTemplate;

    public FraudAnalysisNotifierImpl(KafkaTemplate<String, FraudAnalysisResult> kafkaTemplate) {
        _kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void notifyResult(FraudAnalysisResult result) {
        Message<CustomEvent> message = MessageBuilder
                .withPayload(new CustomEvent(result.getId().toString(), "com.fabiogouw.eventprocessingdemo.FraudAnalysisResult", 1, result))
                .setHeader(KafkaHeaders.TOPIC, TOPIC)
                .setHeader(KafkaHeaders.MESSAGE_KEY, result.getId().toString())
                .setHeader("event_type", "com.fabiogouw.eventprocessingdemo.FraudAnalysisResult")
                .build();
        _logger.info(String.format("#### -> Producing message -> %s", message));
        _kafkaTemplate.send(message);
    }
}
