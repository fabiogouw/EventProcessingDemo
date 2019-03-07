package com.fabiogouw.eventprocessingapp.adapters.sources;

import com.fabiogouw.eventprocessinglib.dtos.CustomEvent;
import com.fabiogouw.eventprocessinglib.ports.EventSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public abstract class SpringKafkaEventSource implements EventSource {

    private final Logger _logger = LoggerFactory.getLogger(SpringKafkaEventSource.class);
    private final KafkaListenerEndpointRegistry _registry;
    private Consumer<CustomEvent> _run;

    protected SpringKafkaEventSource(KafkaListenerEndpointRegistry registry) {
        _registry = registry;
    }

    public abstract String getListenerId();

    protected void run(CustomEvent event) {
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
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        Runnable task = new Runnable() {
            public void run() {
                MessageListenerContainer container =_registry.getListenerContainer(getListenerId());
                if(container != null) {
                    container.start();
                    _logger.info("Kafka subscription {} started...", getListenerId());
                    scheduler.shutdown();
                }
            }
        };
        scheduler.scheduleAtFixedRate(task, 100, 100, TimeUnit.MILLISECONDS);
    }

    @Override
    public void unsubscribe() {
        MessageListenerContainer container =_registry.getListenerContainer(getListenerId());
        if(container != null) {
            _logger.info("Kafka subscription {} stopping...", getListenerId());
            container.stop();
        }
    }
}
