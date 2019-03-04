package com.fabiogouw.eventprocessingapp.adapters.producers;

import com.fabiogouw.eventprocessingapp.adapters.dtos.FraudAnalysisResult;
import com.fabiogouw.eventprocessingapp.adapters.dtos.LimitAnalysisResult;
import com.fabiogouw.eventprocessingapp.ports.FraudAnalysisNotifier;
import com.fabiogouw.eventprocessingapp.ports.LimitAnalysisNotifier;
import com.fabiogouw.eventprocessinglib.dtos.CustomEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

public class LimitAnalysisNotifierImpl implements LimitAnalysisNotifier {

    private static final Logger _logger = LoggerFactory.getLogger(LimitAnalysisNotifierImpl.class);
    private static final String TOPIC = "withdraws";

    private KafkaTemplate<String, LimitAnalysisResult> _kafkaTemplate;

    public LimitAnalysisNotifierImpl(KafkaTemplate<String, LimitAnalysisResult> kafkaTemplate) {
        _kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void notifyResult(LimitAnalysisResult result) {
        Message<CustomEvent> message = MessageBuilder
                .withPayload(new CustomEvent(result.getId().toString(), "com.fabiogouw.eventprocessingdemo.LimitAnalysisResult", 1, result))
                .setHeader(KafkaHeaders.TOPIC, TOPIC)
                .setHeader(KafkaHeaders.MESSAGE_KEY, result.getId().toString())
                .setHeader("event_type", "com.fabiogouw.eventprocessingdemo.LimitAnalysisResult")
                .build();
        _logger.info(String.format("#### -> Producing message -> %s", message));
        _kafkaTemplate.send(message);
    }
}
