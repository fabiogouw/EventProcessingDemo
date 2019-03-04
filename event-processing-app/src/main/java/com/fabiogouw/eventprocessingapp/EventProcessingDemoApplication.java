package com.fabiogouw.eventprocessingapp;

import com.fabiogouw.adapters.KafkaJoinNotifier;
import com.fabiogouw.adapters.KafkaRewindableEventSource;
import com.fabiogouw.adapters.RedisJoinStateRepository;
import com.fabiogouw.domain.JoinManagerImpl;
import com.fabiogouw.domain.valueObjects.CommandState;
import com.fabiogouw.eventprocessinglib.adapters.services.EventConsumerImpl;
import com.fabiogouw.eventprocessinglib.ports.EventConsumer;
import com.fabiogouw.eventprocessinglib.ports.EventHandler;
import com.fabiogouw.eventprocessinglib.ports.EventSource;
import com.fabiogouw.ports.JoinNotifier;
import com.fabiogouw.ports.JoinManager;
import io.micrometer.core.instrument.Timer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import redis.clients.jedis.Jedis;

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
    @Qualifier("fraudAndLimitJoinForWithdraw")
    public JoinManager getJoin() {
        return new JoinManagerImpl(new RedisJoinStateRepository(new Jedis("172.17.0.2"), 300),
                new KafkaRewindableEventSource(createConsumer(_bootstrapAddress, "join.events")));
    }

    @Bean
    @Qualifier("fraudAndLimitJoinForWithdraw")
    public JoinNotifier getJoinNotifier() {
        return new KafkaJoinNotifier(createProducer(_bootstrapAddress), "join.events");
    }

    private static Consumer<String, CommandState> createConsumer(String bootstrapAddress, String topic) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "join");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1000);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        Consumer<String, CommandState> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topic));
        return consumer;
    }


    public static Producer<String, CommandState> createProducer(String bootstrapAddress) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "joinnotifier");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new KafkaProducer<>(props);
    }
}
