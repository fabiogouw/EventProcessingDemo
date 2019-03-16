package com.fabiogouw.eventprocessingapp.adapters.controllers;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Endpoint(id="kafka")
public class KafkaMetricsEndPoint {

    private final static String PRODUCERS = "producers";
    private final static String CONSUMERS = "consumers";

    private final KafkaTemplate<?,  ?>[] _templates;
    private final KafkaListenerEndpointRegistry _kafkaListenerEndpointRegistry;

    public KafkaMetricsEndPoint(KafkaTemplate<?,  ?>[] templates,
                                KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry) {
        _templates = templates;
        _kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;
    }

    @ReadOperation
    public List<String> getRoot() {
        return Arrays.asList(PRODUCERS, CONSUMERS);
    }

    @ReadOperation
    public Map<String, Map<String, String>> getMetricsForType(@Selector String type) {
        switch (type) {
            case CONSUMERS :
                return getMessageListenerContainerMetrics();
            case PRODUCERS:
                return getKafkaTemplateMetrics();
            default:
                return new HashMap<>();
        }
    }

    @ReadOperation
    public Map<String, String> getMetricsForTypeById(@Selector String type, @Selector String id) {
        switch (type) {
            case CONSUMERS :
                return getMessageListenerContainerMetricsById(id);
            case PRODUCERS:
                return getKafkaTemplateMetricsById(id);
            default:
                return new HashMap<>();
        }
    }

    private Map<String, Map<String, String>> getKafkaTemplateMetrics() {
        Map<String, Map<String, String>> allMetrics = new LinkedHashMap<>();
        int i = 0;
        for (KafkaTemplate<?,  ?> template :_templates) {
            Map<String, String> metrics = new HashMap<>();
            template.metrics().forEach((a, b) -> {
                metrics.put(b.metricName().name(), b.metricValue().toString());
            });
            allMetrics.put(i + template.getDefaultTopic(), metrics);
            i++;
        }
        return allMetrics;
    }

    private Map<String, Map<String, String>> getMessageListenerContainerMetrics() {
        Map<String, Map<String, String>> allMetrics = new LinkedHashMap<>();
        Collection<MessageListenerContainer> containers = _kafkaListenerEndpointRegistry.getListenerContainers();
        for(MessageListenerContainer container : containers) {
            String id = container.getContainerProperties().getGroupId();
            Map<String, String> metrics = getMessageListenerContainerMetricsById(id);
            allMetrics.put(id, metrics);
        }
        return allMetrics;
    }

    private Map<String, String> getKafkaTemplateMetricsById(String id) {
        Map<String, String> metrics = new LinkedHashMap<>();
//        for (KafkaTemplate<?,  ?> template :_templates) {
//        }
//        MessageListenerContainer container = _kafkaListenerEndpointRegistry.getListenerContainer(id);
//
//        container.metrics().forEach((a, b) -> {
//            b.forEach((c,  d) -> {
//                metrics.put(c.name(), d.metricValue().toString());
//            });
//        });
        return metrics;
    }

    private Map<String, String> getMessageListenerContainerMetricsById(String id) {
        MessageListenerContainer container = _kafkaListenerEndpointRegistry.getListenerContainer(id);
        Map<String, String> metrics = new LinkedHashMap<>();
        if(container != null) {
            container.metrics().forEach((a, b) -> {
                b.forEach((c,  d) -> {
                    metrics.put(c.name(), d.metricValue().toString());
                });
            });
        }
        return metrics;
    }
}
