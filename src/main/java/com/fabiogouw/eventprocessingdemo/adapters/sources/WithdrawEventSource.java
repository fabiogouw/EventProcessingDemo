package com.fabiogouw.eventprocessingdemo.adapters.sources;

import com.fabiogouw.eventprocessingdemo.adapters.dtos.CustomEvent;
import com.fabiogouw.eventprocessingdemo.ports.EventSource;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.Acknowledgment;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class WithdrawEventSource implements EventSource {

    private final Logger _logger = LoggerFactory.getLogger(WithdrawEventSource.class);
    private static final String LISTENER_ID = "WithdrawEventSource";
    private final KafkaListenerEndpointRegistry _registry;
    private Consumer<CustomEvent> _run;

    public WithdrawEventSource(KafkaListenerEndpointRegistry registry) {
        _registry = registry;
    }

    @KafkaListener(id = LISTENER_ID, topics = {"transfers", "withdraws"}, containerFactory = "containerFactory")
    public void listen(ConsumerRecord<String, CustomEvent> message, Acknowledgment acknowledgment) {
        _logger.info("#### -> Consumed message -> '{}' : '{}' / '{}'", message.topic(),  message.partition(), message.offset());
        CustomEvent event = message.value();
        _run.accept(event);
        acknowledgment.acknowledge();
    }

    @Override
    public void subscribe(Consumer<CustomEvent> run) {
        _run = run;
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        Runnable task = new Runnable() {
            public void run() {
                MessageListenerContainer container =_registry.getListenerContainer(LISTENER_ID);
                if(container != null) {
                    container.start();
                    _logger.info("Kafka subscription started...");
                    scheduler.shutdown();
                }
            }
        };
        scheduler.scheduleAtFixedRate(task, 100, 100, TimeUnit.MILLISECONDS);
    }
}
