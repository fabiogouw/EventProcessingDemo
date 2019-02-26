package com.fabiogouw.eventprocessingapp.adapters.producers;

import com.fabiogouw.eventprocessingapp.adapters.dtos.Debit;
import com.fabiogouw.eventprocessingapp.adapters.dtos.Transfer;
import com.fabiogouw.eventprocessingapp.adapters.dtos.Withdraw;
import com.fabiogouw.eventprocessingapp.ports.DebitNotifier;
import com.fabiogouw.eventprocessingapp.ports.TransferNotifier;
import com.fabiogouw.eventprocessingapp.ports.WithdrawNotifier;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class NotifierBeans {

    @Value(value = "${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapAddress = "172.19.0.3:9092";

    @Bean
    public KafkaTemplate<String, Withdraw> createWithdrawTemplate() {
        Map<String, Object> senderProps = senderProps();
        ProducerFactory<String, Withdraw> pf = new DefaultKafkaProducerFactory<>(senderProps);
        KafkaTemplate<String, Withdraw> template = new KafkaTemplate<>(pf);
        return template;
    }

    @Bean
    public KafkaTemplate<String, Transfer> createTransferTemplate() {
        Map<String, Object> senderProps = senderProps();
        ProducerFactory<String, Transfer> pf = new DefaultKafkaProducerFactory<>(senderProps);
        KafkaTemplate<String, Transfer> template = new KafkaTemplate<>(pf);
        return template;
    }

    @Bean
    public KafkaTemplate<String, Debit> createDebirTemplate() {
        Map<String, Object> senderProps = senderProps();
        ProducerFactory<String, Debit> pf = new DefaultKafkaProducerFactory<>(senderProps);
        KafkaTemplate<String, Debit> template = new KafkaTemplate<>(pf);
        return template;
    }

    private Map<String, Object> senderProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    @Bean
    public DebitNotifier getDebitNotifier() {
        return new DebitNotifierImpl();
    }

    @Bean
    public TransferNotifier getTransferNotifier(KafkaTemplate<String, Transfer> kafkaTemplate) {
        return new TransferNotifierImpl(kafkaTemplate);
    }

    @Bean
    public WithdrawNotifier getWithdrawNotifier(KafkaTemplate<String, Withdraw> kafkaTemplate) {
        return new WithdrawNotifierImpl(kafkaTemplate);
    }
}