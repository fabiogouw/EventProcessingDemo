package com.fabiogouw.adapters;

import com.fabiogouw.domain.valueObjects.CommandState;
import com.fabiogouw.ports.JoinNotifier;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class KafkaJoinNotifier implements JoinNotifier {

    private final Producer<String, CommandState> _producer;

    public KafkaJoinNotifier(Producer<String, CommandState> producer) {
        _producer = producer;
    }

    @Override
    public void notify(String id, String eventType, Object payload) {
        ProducerRecord<String, CommandState> record = new ProducerRecord<>("join.events", id, new CommandState(id, eventType, payload));
        _producer.send(record);
    }
}
