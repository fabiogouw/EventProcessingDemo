package com.fabiogouw.eventprocessingapp.adapters.ioc;

import com.fabiogouw.eventprocessingapp.adapters.controllers.AsyncHolderImpl;
import com.fabiogouw.eventprocessingapp.core.dtos.Withdraw;
import com.fabiogouw.eventprocessingapp.adapters.handlers.FraudAnalysisEventHandler;
import com.fabiogouw.eventprocessingapp.adapters.handlers.LimitAnalysisEventHandler;
import com.fabiogouw.eventprocessingapp.adapters.handlers.WithdrawRequestEventHandler;
import com.fabiogouw.eventprocessingapp.adapters.sources.FraudAnalysisEventSource;
import com.fabiogouw.eventprocessingapp.adapters.sources.LimitAnalysisEventSource;
import com.fabiogouw.eventprocessingapp.adapters.sources.WebEventSource;
import com.fabiogouw.eventprocessingapp.adapters.sources.WithdrawEventSource;
import com.fabiogouw.eventprocessingapp.core.ports.FraudAnalysisNotifier;
import com.fabiogouw.eventprocessingapp.core.ports.Holder;
import com.fabiogouw.eventprocessingapp.core.ports.LimitAnalysisNotifier;
import com.fabiogouw.eventprocessingapp.core.ports.WithdrawNotifier;
import com.fabiogouw.eventprocessinglib.adapters.services.EventConsumerImpl;
import com.fabiogouw.eventprocessinglib.adapters.services.IgnoreMessageErrorHandler;
import com.fabiogouw.eventprocessinglib.adapters.services.MicrometerEventHandlerMetricImpl;
import com.fabiogouw.eventprocessinglib.core.dtos.CustomEvent;
import com.fabiogouw.eventprocessinglib.core.ports.EventConsumer;
import com.fabiogouw.eventprocessinglib.core.ports.EventHandler;
import com.fabiogouw.eventprocessinglib.core.ports.EventHandlerMetric;
import com.fabiogouw.eventprocessinglib.core.ports.EventSource;
import com.fabiogouw.domain.ports.ReactiveStateMachineEventNotifier;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.function.Function;

@EnableKafka
@Configuration
public class EventSourcesConfigBeans {

    @Value(value = "${spring.kafka.consumer.bootstrap-servers}")
    private String _bootstrapAddress;

    @Value(value = "${spring.kafka.consumer.group-id}")
    private String _groupId;

    @Value(value = "${spring.kafka.schema-registry}")
    private String _schemaRegistryAddress;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CustomEvent> containerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, _bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, _groupId);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 100);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        //props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
        //props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, _schemaRegistryAddress);
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
    public ConcurrentKafkaListenerContainerFactory<String, Withdraw> withdrawContainerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, _bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, _groupId);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 100);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        //props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
        //props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, _schemaRegistryAddress);
        ConcurrentKafkaListenerContainerFactory<String, Withdraw> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setAutoStartup(false);
        factory.setConcurrency(11);
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(props));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.getContainerProperties().setSyncCommits(true);
        factory.setErrorHandler(new IgnoreMessageErrorHandler());
        return factory;
    }

    @Bean
    @Qualifier("WebWithdraw")
    public WebEventSource<Withdraw> getWithdrawWebEventSource() {
        return new WebEventSource<Withdraw>();
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
    @Qualifier("Withdraw")
    public EventHandler getWithdrawEventHandler(WithdrawNotifier withdrawNotifier, ReactiveStateMachineEventNotifier reactiveStateMachineEventNotifier, ObjectMapper mapper) {
        return new WithdrawRequestEventHandler(withdrawNotifier, reactiveStateMachineEventNotifier, mapper);
    }

    @Bean
    @Qualifier("WithdrawFraudAnalysis")
    public EventHandler getWithdrawFraudAnalysisEventHandler(FraudAnalysisNotifier fraudAnalysisNotifier, ReactiveStateMachineEventNotifier reactiveStateMachineEventNotifier, ObjectMapper mapper) {
        return new FraudAnalysisEventHandler(fraudAnalysisNotifier, reactiveStateMachineEventNotifier, mapper);
    }

    @Bean
    @Qualifier("WithdrawLimitAnalysis")
    public EventHandler getWithdrawLimitAnalysisEventHandler(LimitAnalysisNotifier limitAnalysisNotifier, ReactiveStateMachineEventNotifier reactiveStateMachineEventNotifier, ObjectMapper mapper) {
        return new LimitAnalysisEventHandler(limitAnalysisNotifier, reactiveStateMachineEventNotifier, mapper);
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

    @Bean
    public Holder<Withdraw> getWithdrawHolder() {
        return new AsyncHolderImpl<>();
    }
}
