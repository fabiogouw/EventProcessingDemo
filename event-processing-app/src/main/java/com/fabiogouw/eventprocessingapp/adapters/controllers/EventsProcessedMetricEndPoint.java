package com.fabiogouw.eventprocessingapp.adapters.controllers;

import io.micrometer.core.instrument.Timer;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Endpoint(id="events-processed")
public class EventsProcessedMetricEndPoint {

    private final Map<String, Timer> _eventMetrics;

    public EventsProcessedMetricEndPoint(Map<String, Timer> eventMetrics) {
        _eventMetrics = eventMetrics;
    }

    @ReadOperation
    public Map<String, Map<String, String>> getEventsProcessedMetrics() {
        Map<String, Map<String, String>> allMetrics = new LinkedHashMap<>();
        _eventMetrics.forEach((eventType, timer) -> {
            allMetrics.put(eventType, getEventsProcessedMetricsById(eventType));
        });
        return allMetrics;
    }

    @ReadOperation
    public Map<String, String> getEventsProcessedMetricsById(@Selector String eventType) {
        Map<String, String> metrics = new LinkedHashMap<>();
        Timer timer = _eventMetrics.get(eventType);
        if(timer != null) {
            double totalTime = timer.totalTime(TimeUnit.MILLISECONDS);
            double eventsProcessed = timer.count();
            if(timer.count() > 0) {
                metrics.put("TotalTimeProcessingEventsInMilliseconds", String.valueOf(totalTime));
                metrics.put("EventsProcessed", String.valueOf(eventsProcessed));
                double averageTimePerEvent = totalTime / eventsProcessed;
                metrics.put("AverageTimePerEventInMilliseconds", String.valueOf(averageTimePerEvent));
                double tps = eventsProcessed / totalTime * 1000;
                metrics.put("TPS", String.valueOf(tps));
            }
        }
        return metrics;
    }
}
