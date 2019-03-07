package com.fabiogouw.eventprocessinglib.adapters.services;

import com.fabiogouw.eventprocessinglib.dtos.CustomEvent;
import com.fabiogouw.eventprocessinglib.ports.EventConsumer;
import com.fabiogouw.eventprocessinglib.ports.EventHandler;
import com.fabiogouw.eventprocessinglib.ports.EventSource;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EventConsumerImpl implements EventConsumer {

    private final Logger _logger = LoggerFactory.getLogger(EventConsumerImpl.class);

    private final List<EventHandler> _handlers;
    private final List<EventSource> _sources;
    private final Timer _timer;

    public EventConsumerImpl(List<EventHandler> handlers, List<EventSource> sources, Timer timer) {
        _handlers = handlers;
        _sources = sources;
        _timer = timer;
    }

    public void consume(CustomEvent event) {
        _timer.record(() -> {
            boolean processed = false;
            for(EventHandler eventHandler : _handlers) {
                if(eventHandler.getType().equals(event.getType())
                        && eventHandler.getLowestVersion() >= event.getVersion()
                        && eventHandler.getHighestVersion() <= event.getVersion()) {
                    eventHandler.handle(event);
                    processed = true;
                }
            }
            if(!processed) {
                _logger.warn("Event '{}' (version {}} ignored because no handler can fit it...", event.getType(), event.getVersion());
            }
        });
    }

    @Override
    public void start() {
        for (EventSource source : _sources) {
            source.subscribe(this::consume);
        }
        _logger.info("Event sources loaded and listening to: '{}' ...", String.join(", ", _sources.stream().map(s -> s.getClass().getName()).toArray(String[]::new)));
        _logger.info("Event handlers loaded and waiting for events: '{}' ...", String.join(", ", _handlers.stream().map(s -> s.getClass().getName()).toArray(String[]::new)));
    }

    @Override
    public void stop() {
        for (EventSource source : _sources) {
            source.unsubscribe();
        }
    }
}