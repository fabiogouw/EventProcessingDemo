package com.fabiogouw.eventprocessingapp.adapters.sources;

import com.fabiogouw.eventprocessingapp.adapters.handlers.WithdrawFraudAnalysisEventHandler;
import com.fabiogouw.eventprocessingapp.adapters.handlers.WithdrawLimitAnalysisEventHandler;
import com.fabiogouw.eventprocessingapp.ports.DebitNotifier;
import com.fabiogouw.eventprocessinglib.adapters.services.IgnoreMessageErrorHandler;
import com.fabiogouw.eventprocessinglib.dtos.CustomEvent;
import com.fabiogouw.eventprocessinglib.ports.EventHandler;
import com.fabiogouw.eventprocessinglib.ports.EventSource;
import com.fabiogouw.ports.JoinNotifier;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class EventSourcesConfigBeans {

    @Value(value = "${spring.kafka.consumer.bootstrap-servers}")
    private String bootstrapAddress;

    @Value(value = "${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CustomEvent> containerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 100);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        ConcurrentKafkaListenerContainerFactory<String, CustomEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setAutoStartup(false);
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(props));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.getContainerProperties().setSyncCommits(true);
        factory.setErrorHandler(new IgnoreMessageErrorHandler());
        return factory;
    }

    @Bean
    @Qualifier("WithdrawFraudAnalysis")
    public EventHandler getWithdrawFraudAnalysisEventHandler(JoinNotifier joinNotifier) {
        return new WithdrawFraudAnalysisEventHandler(joinNotifier);
    }

    @Bean
    @Qualifier("WithdrawLimitAnalysis")
    public EventHandler getWithdrawLimitAnalysisEventHandler(JoinNotifier joinNotifier) {
        return new WithdrawLimitAnalysisEventHandler(joinNotifier);
    }

    @Bean
    @Qualifier("Withdraw")
    public EventSource getWithdrawEventSource(KafkaListenerEndpointRegistry registry) {
        return new WithdrawEventSource(registry);
    }

    @Bean
    public Timer getTimer() {
        MeterRegistry registry = new SimpleMeterRegistry();
        Metrics.addRegistry(registry);
        Timer timer = Timer.builder("app.eventsProcessed").tag("method", "manual").register(registry);
        return timer;
    }
}
