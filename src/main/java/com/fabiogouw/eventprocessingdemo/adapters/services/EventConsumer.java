package com.fabiogouw.eventprocessingdemo.adapters.services;

import com.fabiogouw.eventprocessingdemo.adapters.dtos.CustomEvent;
import com.fabiogouw.eventprocessingdemo.ports.EventHandler;
import com.fabiogouw.eventprocessingdemo.ports.EventSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventConsumer {

    private final Logger _logger = LoggerFactory.getLogger(EventConsumer.class);

    private final List<EventHandler> _handlers;
    private final List<EventSource> _sources;

    public EventConsumer(List<EventHandler> handlers, List<EventSource> sources) {
        _handlers = handlers;
        _sources = sources;
        for (EventSource source : _sources) {
            source.subscribe(this::consume);
        }
    }

    public void consume(CustomEvent event) {
        boolean processed = false;
        for(EventHandler eventHandler : _handlers) {
            if(eventHandler.getType().equals(event.getType())
                && eventHandler.getLowestVersion() >= event.getVersion()
                && eventHandler.getHighestVersion() <= event.getVersion()) {
                processed = true;
                eventHandler.handle(event);
                break;
            }
        }
        if(!processed) {
            _logger.warn(String.format("Evento '%s' ignorado pois nenhum handler o atende...", event.getType()));
        }
    }
}