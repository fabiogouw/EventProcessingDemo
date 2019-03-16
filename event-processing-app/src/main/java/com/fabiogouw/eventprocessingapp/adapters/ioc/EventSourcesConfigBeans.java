package com.fabiogouw.eventprocessingapp.adapters.ioc;

import com.fabiogouw.adapters.DefaultJoinEventHandler;
import com.fabiogouw.eventprocessingapp.adapters.handlers.FraudAnalysisEventHandler;
import com.fabiogouw.eventprocessingapp.adapters.handlers.LimitAnalysisEventHandler;
import com.fabiogouw.eventprocessingapp.adapters.sources.FraudAnalysisEventSource;
import com.fabiogouw.eventprocessingapp.adapters.sources.LimitAnalysisEventSource;
import com.fabiogouw.eventprocessingapp.adapters.sources.WithdrawEventSource;
import com.fabiogouw.eventprocessingapp.ports.FraudAnalysisNotifier;
import com.fabiogouw.eventprocessingapp.ports.LimitAnalysisNotifier;
import com.fabiogouw.eventprocessinglib.adapters.services.EventConsumerImpl;
import com.fabiogouw.eventprocessinglib.adapters.services.IgnoreMessageErrorHandler;
import com.fabiogouw.eventprocessinglib.adapters.services.MicrometerEventHandlerMetricImpl;
import com.fabiogouw.eventprocessinglib.dtos.CustomEvent;
import com.fabiogouw.eventprocessinglib.ports.EventConsumer;
import com.fabiogouw.eventprocessinglib.ports.EventHandler;
import com.fabiogouw.eventprocessinglib.ports.EventHandlerMetric;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Supplier;

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
        factory.setConcurrency(11);
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(props));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.getContainerProperties().setSyncCommits(true);
        factory.setErrorHandler(new IgnoreMessageErrorHandler());
        return factory;
    }

    @Bean
    @Qualifier("Withdraw")
    public EventSource getWithdrawEventSource(KafkaListenerEndpointRegistry registry) {
        return new WithdrawEventSource(registry);
    }

    @Bean
    @Qualifier("FraudAnalysis")
    public EventSource getFraudAnalysisEventSource(KafkaListenerEndpointRegistry registry) {
        return new FraudAnalysisEventSource(registry);
    }

    @Bean
    @Qualifier("LimitAnalysis")
    public EventSource geLimitAnalysisEventSource(KafkaListenerEndpointRegistry registry) {
        return new LimitAnalysisEventSource(registry);
    }

    @Bean
    @Qualifier("WithdrawFraudAnalysis")
    public EventHandler getWithdrawFraudAnalysisEventHandler(FraudAnalysisNotifier fraudAnalysisNotifier) {
        return new FraudAnalysisEventHandler(fraudAnalysisNotifier);
    }

    @Bean
    @Qualifier("WithdrawLimitAnalysis")
    public EventHandler getWithdrawLimitAnalysisEventHandler(LimitAnalysisNotifier limitAnalysisNotifier) {
        return new LimitAnalysisEventHandler(limitAnalysisNotifier);
    }

    @Bean
    @Qualifier("withdrawDebitJoinEventHandlers")
    public EventHandler getWithdrawFraudAnalysisEventHandlerForDebitJoin(@Qualifier("fraudAndLimitJoinForWithdraw") JoinNotifier joinNotifier) {
        return new DefaultJoinEventHandler(joinNotifier, "com.fabiogouw.eventprocessingdemo.FraudAnalysisResult", 1, 1);
    }

    @Bean
    @Qualifier("withdrawDebitJoinEventHandlers")
    public EventHandler getWithdrawLimitAnalysisEventHandlerForDebitJoin(@Qualifier("fraudAndLimitJoinForWithdraw") JoinNotifier joinNotifier) {
        return new DefaultJoinEventHandler(joinNotifier, "com.fabiogouw.eventprocessingdemo.LimitAnalysisResult", 1, 1);
    }

    @Bean
    public Function<String, Timer> getTimerFactory() {
        return (type) -> {
            MeterRegistry registry = new SimpleMeterRegistry();
            Metrics.addRegistry(registry);
            Timer timer = Timer.builder("app.eventsProcessed." + type).tag("method", "manual").register(registry);
            return timer;
        };
    }

    @Bean
    public EventHandlerMetric getEventHandlerMetric(Function<String, Timer> timerFactory) {
        return new MicrometerEventHandlerMetricImpl(timerFactory);
    }

    @Bean
    public EventConsumer getEventConsumer(List<EventHandler> handlers, List<EventSource> sources, EventHandlerMetric metrics) {
        return new EventConsumerImpl(handlers, sources, metrics);
    }
}
