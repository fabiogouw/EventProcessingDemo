package com.fabiogouw.eventprocessinglib.adapters.services;

import com.fabiogouw.eventprocessinglib.core.ports.EventHandlerMetric;
import io.micrometer.core.instrument.Timer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MicrometerEventHandlerMetricImpl implements EventHandlerMetric {

    private class TimerPerThread extends ConcurrentHashMap<Long, Timer> {

    }

    private class ThreadPerVersion extends ConcurrentHashMap<Integer, TimerPerThread> {

    }

    private class VersionPerEvent extends ConcurrentHashMap<String, ThreadPerVersion> {

    }

    private final static String TotalTimeProcessingEventsInMilliseconds = "TotalTimeProcessingEventsInMilliseconds";
    private final static String EventsProcessed = "EventsProcessed";
    private final static String AverageTimePerEventInMilliseconds = "AverageTimePerEventInMilliseconds";
    private final static String TPS = "TPS";
    private final static String Threads = "ThreadIds";

    private final Function<String, Timer> _timerFactory;
    private final VersionPerEvent _eventMetrics;

    public MicrometerEventHandlerMetricImpl(Function<String, Timer> timerFactory) {
        _timerFactory = timerFactory;
        _eventMetrics = new VersionPerEvent();
    }

    @Override
    public void record(String eventType, int version, Consumer recorder) {
        Timer timer = getTimerForMetrics(eventType, version);
        timer.record(() -> {
            recorder.accept(1);
        });
    }

    private Timer getTimerForMetrics(String eventType, int version) {
        if(!_eventMetrics.containsKey(eventType)) {
            _eventMetrics.put(eventType, new ThreadPerVersion());
        }
        ThreadPerVersion versionMetrics = _eventMetrics.get(eventType);
        if(!versionMetrics.containsKey(version)) {
            versionMetrics.put(version, new TimerPerThread());
        }
        TimerPerThread timerPerThread = versionMetrics.get(version);
        long threadId = Thread.currentThread().getId();
        if(!timerPerThread.containsKey(threadId)) {
            timerPerThread.put(threadId, _timerFactory.apply(eventType));
        }
        return timerPerThread.get(threadId);
    }

    public List<String> getAllEventsProcessed() {
        return _eventMetrics.entrySet().stream().map(e -> e.getKey()).collect(Collectors.toList());
    }

    public List<Integer> getAllVersionsForEvent(String eventType) {
        final ThreadPerVersion threadPerVersion = _eventMetrics.get(eventType);
        if(threadPerVersion != null) {
            return threadPerVersion.entrySet().stream().map(v -> v.getKey()).collect(Collectors.toList());
        }
        else {
            return Arrays.asList();
        }
    }

    public Map<String, Object> getMetricsByEventTypeAndVersion(String eventType, int version) {
        Map<String, Object> metrics = new LinkedHashMap<>();
        ThreadPerVersion threadPerVersion = _eventMetrics.get(eventType);
        if(threadPerVersion != null) {
            TimerPerThread timerPerThread = threadPerVersion.get(version);
            if(timerPerThread != null) {
                double totalTime = 0;
                double eventsProcessed = 0;
                double tps = 0;
                List<String> threads = new ArrayList<>();
                for(Map.Entry<Long, Timer> thread : timerPerThread.entrySet()) {
                    Map<String, Object> metrics2 = populateMetrics(thread.getValue());
                    totalTime += (double)metrics2.get(TotalTimeProcessingEventsInMilliseconds);
                    eventsProcessed += (long)metrics2.get(EventsProcessed);
                    tps += eventsProcessed / totalTime * 1000;
                    threads.add(thread.getKey().toString());
                }
                double averageTimePerEvent = totalTime / eventsProcessed;
                metrics.put(TotalTimeProcessingEventsInMilliseconds, totalTime);
                metrics.put(EventsProcessed, eventsProcessed);
                metrics.put(AverageTimePerEventInMilliseconds, averageTimePerEvent);
                metrics.put(TPS, tps);
                metrics.put(Threads, String.join(", ", threads));
            }
        }
        return metrics;
    }

    private Map<String, Object> populateMetrics(Timer timer) {
        Map<String, Object> metrics = new LinkedHashMap<>();
        if(timer != null) {
            double totalTime = timer.totalTime(TimeUnit.MILLISECONDS);
            long eventsProcessed = timer.count();
            if(timer.count() > 0) {
                metrics.put(TotalTimeProcessingEventsInMilliseconds, totalTime);
                metrics.put(EventsProcessed, eventsProcessed);
                double averageTimePerEvent = totalTime / eventsProcessed;
                metrics.put(AverageTimePerEventInMilliseconds, averageTimePerEvent);
                double tps = eventsProcessed / totalTime * 1000;
                metrics.put(TPS, tps);
            }
        }
        return metrics;
    }
}
