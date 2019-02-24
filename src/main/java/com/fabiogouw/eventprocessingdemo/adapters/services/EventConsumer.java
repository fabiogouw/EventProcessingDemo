package com.fabiogouw.eventprocessingdemo.adapters.services;

import com.fabiogouw.eventprocessingdemo.adapters.dtos.CustomEvent;
import com.fabiogouw.eventprocessingdemo.ports.EventHandler;
import com.fabiogouw.eventprocessingdemo.ports.EventSource;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class EventConsumer {

    private final Logger _logger = LoggerFactory.getLogger(EventConsumer.class);

    private final List<EventHandler> _handlers;
    private final List<EventSource> _sources;
    private final Timer _timer;

    public EventConsumer(List<EventHandler> handlers, List<EventSource> sources, Timer timer) {
        _handlers = handlers;
        _sources = sources;
        for (EventSource source : _sources) {
            source.subscribe(this::consume);
        }
        _timer = timer;
    }

    public void consume(CustomEvent event) {
        _timer.record(() -> {
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
                _logger.warn("Event '{}' (version {}} ignored because no handler can fit it...", event.getType(), event.getVersion());
            }
        });
    }
}