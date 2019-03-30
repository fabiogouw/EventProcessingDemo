package com.fabiogouw.eventprocessingapp.adapters.sources;

import com.fabiogouw.eventprocessinglib.core.dtos.CustomEvent;
import com.fabiogouw.eventprocessinglib.core.ports.EventSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class WebEventSource<T> implements EventSource {

    private final Logger _logger = LoggerFactory.getLogger(WebEventSource.class);
    private Consumer<CustomEvent> _run;

    public void send(String correlationId, String eventType, int version, T data) {
        CustomEvent event = new CustomEvent(correlationId, eventType, version, () -> data);
        try {
            _run.accept(event);
        }
        catch (Exception ex) {
            _logger.error("Error while processing event {} ({}:{}). Details: {}", event.getCorrelationId(), event.getType(), event.getVersion(), ex);
        }
    }

    @Override
    public void subscribe(Consumer<CustomEvent> run) {
        _run = run;
    }

    @Override
    public void unsubscribe() {
        _run = null;
    }
}
