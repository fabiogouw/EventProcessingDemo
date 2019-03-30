package com.fabiogouw.eventprocessingapp.adapters.producers;

import com.fabiogouw.eventprocessingapp.core.dtos.LimitAnalysisResult;
import com.fabiogouw.eventprocessingapp.core.ports.LimitAnalysisNotifier;
import com.fabiogouw.eventprocessinglib.core.dtos.CustomEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

public class LimitAnalysisNotifierImpl implements LimitAnalysisNotifier {

    private static final Logger _logger = LoggerFactory.getLogger(LimitAnalysisNotifierImpl.class);
    private static final String TOPIC = "limit";

    private KafkaTemplate<String, LimitAnalysisResult> _kafkaTemplate;

    public LimitAnalysisNotifierImpl(KafkaTemplate<String, LimitAnalysisResult> kafkaTemplate) {
        _kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void notifyResult(LimitAnalysisResult result) {
        Message<LimitAnalysisResult> message = MessageBuilder
                .withPayload(result)
                .setHeader(KafkaHeaders.TOPIC, TOPIC)
                .setHeader(KafkaHeaders.MESSAGE_KEY, result.getCorrelationId())
                .setHeader(CustomEvent.CORRELATION_ID, result.getCorrelationId())
                .setHeader(CustomEvent.EVENT_TYPE, "com.fabiogouw.eventprocessingdemo.LimitAnalysisResult")
                .setHeader(CustomEvent.EVENT_TYPE_VERSION, 1)
                .build();
        _logger.info(String.format("#### -> Producing message -> %s", message));
        _kafkaTemplate.send(message);
    }
}
