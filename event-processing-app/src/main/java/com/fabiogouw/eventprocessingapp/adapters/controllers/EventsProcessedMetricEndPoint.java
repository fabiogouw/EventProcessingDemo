package com.fabiogouw.eventprocessingapp.adapters.controllers;

import com.fabiogouw.eventprocessinglib.dtos.EventsProcessedMetric;
import io.micrometer.core.instrument.Timer;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Endpoint(id="events-processed")
public class EventsProcessedMetricEndPoint {

    private final Timer _timer;

    public EventsProcessedMetricEndPoint(Timer timer) {
        _timer = timer;
    }

    @ReadOperation
    public EventsProcessedMetric getEventsProcessedMetrics() {
        Map<String, Double> details = new LinkedHashMap<>();
        double totalTime = _timer.totalTime(TimeUnit.MILLISECONDS);
        double eventsProcessed = _timer.count();
        if(_timer.count() > 0) {
            details.put("TotalTimeProcessingEventsInMilliseconds", totalTime);
            details.put("EventsProcessed", eventsProcessed);
            double averageTimePerEvent = totalTime / eventsProcessed;
            details.put("AverageTimePerEventInMilliseconds", averageTimePerEvent);
            double tps = eventsProcessed / totalTime * 1000;
            details.put("TPS", tps);
        }
        return new EventsProcessedMetric(details);
    }
}
