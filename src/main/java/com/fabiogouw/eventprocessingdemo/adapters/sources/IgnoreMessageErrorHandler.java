package com.fabiogouw.eventprocessingdemo.adapters.sources;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.ErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;

import java.util.List;

public class IgnoreMessageErrorHandler implements ErrorHandler {

    private final Logger _logger = LoggerFactory.getLogger(IgnoreMessageErrorHandler.class);
    @Override
    public void handle(Exception e, ConsumerRecord<?, ?> consumerRecord) {

    }

    @Override
    public void handle(Exception thrownException, List<ConsumerRecord<?,?>> records, Consumer<?,?> consumer, MessageListenerContainer container) {
        String s = thrownException.getMessage().split("Error deserializing key/value for partition ")[1].split(". If needed, please seek past the record to continue consumption.")[0];
        String topics = s.split("-")[0];
        int offset = Integer.valueOf(s.split("offset ")[1]);
        int partition = Integer.valueOf(s.split("-")[1].split(" at")[0]);
        TopicPartition topicPartition = new TopicPartition(topics, partition);
        _logger.error("Skipping " + partition + " offset " + offset);
        consumer.seek(topicPartition, offset + 1);
    }
}
