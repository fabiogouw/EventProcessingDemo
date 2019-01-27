package com.fabiogouw.eventprocessingdemo.adapters.services;

import com.fabiogouw.eventprocessingdemo.adapters.dtos.CustomEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class Consumer {

    private final Logger _logger = LoggerFactory.getLogger(Consumer.class);

    @KafkaListener(topics = "transfers", groupId = "group_id", containerFactory = "containerFactory")
    public void consume(ConsumerRecord<String, CustomEvent> message, Acknowledgment acknowledgment) throws IOException {
        _logger.info(String.format("#### -> Consumed message -> %s", message));
        CustomEvent ce = (CustomEvent)message.value();
        _logger.info(ce.getType());
        acknowledgment.acknowledge();
    }
}