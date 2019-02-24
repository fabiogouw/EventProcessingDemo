package com.fabiogouw.eventprocessingdemo.adapters.dtos.metrics;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EventsProcessedMetric {

    private final Map<String, Double> _details;

    public EventsProcessedMetric(Map<String, Double> details) {
        _details = details;
    }

    @JsonAnyGetter
    public Map<String, Double> getMetricsDetails() {
        return _details;
    }
}