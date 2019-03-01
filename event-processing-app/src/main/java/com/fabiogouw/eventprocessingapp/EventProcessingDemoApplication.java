package com.fabiogouw.eventprocessingapp;

import com.fabiogouw.adapters.KafkaRewindableEventSource;
import com.fabiogouw.adapters.RedisJoinerStateRepository;
import com.fabiogouw.domain.JoinerImpl;
import com.fabiogouw.domain.State;
import com.fabiogouw.eventprocessinglib.adapters.services.EventConsumerImpl;
import com.fabiogouw.eventprocessinglib.ports.EventConsumer;
import com.fabiogouw.eventprocessinglib.ports.EventHandler;
import com.fabiogouw.eventprocessinglib.ports.EventSource;
import com.fabiogouw.ports.Joiner;
import io.micrometer.core.instrument.Timer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

@SpringBootApplication
public class EventProcessingDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventProcessingDemoApplication.class, args);
    }

    @Value(value = "${spring.kafka.consumer.bootstrap-servers}")
    private String _bootstrapAddress;

    @Bean
    public EventConsumer getEventConsumer(List<EventHandler> handlers, List<EventSource> sources, Timer timer) {
        return new EventConsumerImpl(handlers, sources, timer);
    }

    @Bean
    public Joiner getJoiner() {
        return new JoinerImpl(new RedisJoinerStateRepository(), new KafkaRewindableEventSource(createConsumer(_bootstrapAddress)));
    }

    private static Consumer<String, State> createConsumer(String bootstrapAddress) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "joiner");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class.getName());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1000);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        Consumer<String, State> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("joiner"));
        return consumer;
    }

}
