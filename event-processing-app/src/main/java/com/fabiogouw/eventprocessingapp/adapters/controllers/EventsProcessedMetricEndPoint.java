package com.fabiogouw.eventprocessingapp.adapters.controllers;

import com.fabiogouw.eventprocessinglib.ports.EventHandlerMetric;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Endpoint(id="events-processed")
public class EventsProcessedMetricEndPoint {

    private final EventHandlerMetric _eventMetrics;

    public EventsProcessedMetricEndPoint(EventHandlerMetric eventMetrics) {
        _eventMetrics = eventMetrics;
    }

    @ReadOperation
    public List<String> getAllEvents() {
        return _eventMetrics.getAllEventsProcessed();
    }

    @ReadOperation
    public List<Integer> getAllVersionsForEvent(@Selector String eventType) {
        return _eventMetrics.getAllVersionsForEvent(eventType);
    }

    @ReadOperation
    public Map<String, Object> getMetricsForEventAndVersion(@Selector String eventType, @Selector Integer version) {
        return _eventMetrics.getMetricsByEventTypeAndVersion(eventType, version);
    }
}
