package com.fabiogouw.adapters;

import com.fabiogouw.domain.valueObjects.CommandState;
import com.fabiogouw.domain.ports.JoinNotifier;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class KafkaJoinNotifier implements JoinNotifier {

    private final Producer<String, CommandState> _producer;
    private final String _topic;

    public KafkaJoinNotifier(Producer<String, CommandState> producer, String topic) {
        _producer = producer;
        _topic = topic;
    }

    @Override
    public void notify(String id, String eventType, Object payload) {
        ProducerRecord<String, CommandState> record = new ProducerRecord<>(_topic, id, new CommandState(id, eventType, payload));
        _producer.send(record);
    }
}
