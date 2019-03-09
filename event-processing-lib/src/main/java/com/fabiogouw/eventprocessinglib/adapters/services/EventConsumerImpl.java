package com.fabiogouw.eventprocessinglib.adapters.services;

import com.fabiogouw.eventprocessinglib.dtos.CustomEvent;
import com.fabiogouw.eventprocessinglib.ports.EventConsumer;
import com.fabiogouw.eventprocessinglib.ports.EventHandler;
import com.fabiogouw.eventprocessinglib.ports.EventSource;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class EventConsumerImpl implements EventConsumer {

    private final Logger _logger = LoggerFactory.getLogger(EventConsumerImpl.class);

    private final List<EventHandler> _handlers;
    private final List<EventSource> _sources;
    private final Function<String, Timer> _timerFactory;
    private final Map<String, Timer> _eventMetrics;

    public EventConsumerImpl(List<EventHandler> handlers,
                             List<EventSource> sources,
                             Function<String, Timer> timerFactory,
                             Map<String, Timer> eventMetrics) {
        _handlers = handlers;
        _sources = sources;
        _timerFactory = timerFactory;
        _eventMetrics = eventMetrics;
    }

    public void consume(CustomEvent event) {
        Timer timer = getTimerForMetrics(event.getType());
        timer.record(() -> {
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

    private Timer getTimerForMetrics(String eventType) {
        if(!_eventMetrics.containsKey(eventType)) {
            _eventMetrics.put(eventType, _timerFactory.apply(eventType));
        }
        return _eventMetrics.get(eventType);
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