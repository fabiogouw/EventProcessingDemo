package com.fabiogouw.eventprocessinglib.core.ports;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface EventHandlerMetric {
    void record(String eventType, int version, Consumer recorder);
    List<String> getAllEventsProcessed();
    List<Integer> getAllVersionsForEvent(String eventType);
    Map<String, Object> getMetricsByEventTypeAndVersion(String eventType, int version);
}
