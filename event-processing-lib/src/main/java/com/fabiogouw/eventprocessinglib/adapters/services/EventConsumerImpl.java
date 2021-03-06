package com.fabiogouw.eventprocessinglib.adapters.services;

import com.fabiogouw.eventprocessinglib.core.dtos.CustomEvent;
import com.fabiogouw.eventprocessinglib.core.ports.EventConsumer;
import com.fabiogouw.eventprocessinglib.core.ports.EventHandler;
import com.fabiogouw.eventprocessinglib.core.ports.EventHandlerMetric;
import com.fabiogouw.eventprocessinglib.core.ports.EventSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EventConsumerImpl implements EventConsumer {

    private final Logger _logger = LoggerFactory.getLogger(EventConsumerImpl.class);

    private final List<EventHandler> _handlers;
    private final List<EventSource> _sources;
    private final EventHandlerMetric _metrics;

    public EventConsumerImpl(List<EventHandler> handlers,
                             List<EventSource> sources,
                             EventHandlerMetric metrics) {
        _handlers = handlers;
        _sources = sources;
        _metrics = metrics;
    }

    public void consume(CustomEvent event) {
        _metrics.record(event.getType(), event.getVersion(), (s) -> {
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