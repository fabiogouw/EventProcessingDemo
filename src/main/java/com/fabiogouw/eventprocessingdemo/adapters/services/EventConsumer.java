package com.fabiogouw.eventprocessingdemo.adapters.services;

import com.fabiogouw.eventprocessingdemo.adapters.dtos.CustomEvent;
import com.fabiogouw.eventprocessingdemo.adapters.handlers.EventHandler;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EventConsumer {

    private final Logger _logger = LoggerFactory.getLogger(EventConsumer.class);

    private final List<EventHandler> _handlers;

    public EventConsumer(List<EventHandler> handlers) {
        _handlers = handlers;
    }

    @KafkaListener(topics = {"transfers", "withdraws"}, groupId = "group_id", containerFactory = "containerFactory")
    public void consume(ConsumerRecord<String, CustomEvent> message, Acknowledgment acknowledgment) {
        _logger.info(String.format("#### -> Consumed message -> %s", message));
        CustomEvent event = message.value();
        boolean processed = false;
        for(EventHandler eventHandler : _handlers) {
            if(eventHandler.getType().equals(event.getType())) {
                processed |= eventHandler.handle(event);
                break;
            }
        }
        if(!processed) {
            _logger.warn(String.format("Evento '%s' ignorado pois nenhum handler o atende...", event.getType()));
        }
        acknowledgment.acknowledge();
    }
}