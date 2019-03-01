package com.fabiogouw.adapters;

import com.fabiogouw.domain.State;
import com.fabiogouw.ports.JoinNotifier;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class KafkaJoinNotifier implements JoinNotifier {

    private final Producer<String, State> _producer;

    public KafkaJoinNotifier(Producer<String, State> producer) {
        _producer = producer;
    }

    @Override
    public void notify(String id, String eventType) {
        ProducerRecord<String, State> record = new ProducerRecord<String, State>("joiner", id, new State(id, eventType, -1, -1));
        _producer.send(record);
    }
}
