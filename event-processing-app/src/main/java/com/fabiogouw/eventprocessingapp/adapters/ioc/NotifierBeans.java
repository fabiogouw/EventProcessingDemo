package com.fabiogouw.eventprocessingapp.adapters.ioc;

import com.fabiogouw.eventprocessingapp.adapters.dtos.*;
import com.fabiogouw.eventprocessingapp.adapters.producers.DebitNotifierImpl;
import com.fabiogouw.eventprocessingapp.adapters.producers.FraudAnalysisNotifierImpl;
import com.fabiogouw.eventprocessingapp.adapters.producers.LimitAnalysisNotifierImpl;
import com.fabiogouw.eventprocessingapp.adapters.producers.WithdrawNotifierImpl;
import com.fabiogouw.eventprocessingapp.ports.*;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class NotifierBeans {

    @Value(value = "${spring.kafka.producer.bootstrap-servers}")
    private String _bootstrapAddress;

    @Value(value = "${spring.kafka.schema-registry}")
    private String _schemaRegistryAddress;

    @Bean
    public KafkaTemplate<String, Withdraw> createWithdrawTemplate() {
        Map<String, Object> senderProps = senderProps(_bootstrapAddress);
        ProducerFactory<String, Withdraw> pf = new DefaultKafkaProducerFactory<>(senderProps);
        KafkaTemplate<String, Withdraw> template = new KafkaTemplate<>(pf);
        return template;
    }

    @Bean
    public KafkaTemplate<String, FraudAnalysisResult> createWithdrawFraudAnalysisTemplate() {
        Map<String, Object> senderProps = senderProps(_bootstrapAddress);
        ProducerFactory<String, FraudAnalysisResult> pf = new DefaultKafkaProducerFactory<>(senderProps);
        KafkaTemplate<String, FraudAnalysisResult> template = new KafkaTemplate<>(pf);
        return template;
    }

    @Bean
    public KafkaTemplate<String, LimitAnalysisResult> createWithdrawLimitAnalysisTemplate() {
        Map<String, Object> senderProps = senderProps(_bootstrapAddress);
        ProducerFactory<String, LimitAnalysisResult> pf = new DefaultKafkaProducerFactory<>(senderProps);
        KafkaTemplate<String, LimitAnalysisResult> template = new KafkaTemplate<>(pf);
        return template;
    }

    @Bean
    public KafkaTemplate<String, Debit> createDebirTemplate() {
        Map<String, Object> senderProps = senderProps(_bootstrapAddress);
        ProducerFactory<String, Debit> pf = new DefaultKafkaProducerFactory<>(senderProps);
        KafkaTemplate<String, Debit> template = new KafkaTemplate<>(pf);
        return template;
    }

    private Map<String, Object> senderProps(String bootstrapAddress) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
        props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, _schemaRegistryAddress);
        return props;
    }

    @Bean
    public DebitNotifier getDebitNotifier() {
        return new DebitNotifierImpl();
    }

    @Bean
    public WithdrawNotifier getWithdrawNotifier(KafkaTemplate<String, Withdraw> kafkaTemplate) {
        return new WithdrawNotifierImpl(kafkaTemplate);
    }

    @Bean
    public FraudAnalysisNotifier getFraudAnalysisNotifier(KafkaTemplate<String, FraudAnalysisResult> kafkaTemplate) {
        return new FraudAnalysisNotifierImpl(kafkaTemplate);
    }

    @Bean
    public LimitAnalysisNotifier getLimitAnalysisNotifier(KafkaTemplate<String, LimitAnalysisResult> kafkaTemplate) {
        return new LimitAnalysisNotifierImpl(kafkaTemplate);
    }
}
