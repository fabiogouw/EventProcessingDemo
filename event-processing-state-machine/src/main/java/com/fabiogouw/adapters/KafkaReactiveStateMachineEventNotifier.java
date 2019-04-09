package com.fabiogouw.adapters;

import com.fabiogouw.domain.valueObjects.CommandState;
import com.fabiogouw.domain.ports.ReactiveStateMachineEventNotifier;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class KafkaReactiveStateMachineEventNotifier implements ReactiveStateMachineEventNotifier {

    private final Producer<String, CommandState> _producer;
    private final String _topic;

    public KafkaReactiveStateMachineEventNotifier(Producer<String, CommandState> producer, String topic) {
        _producer = producer;
        _topic = topic;
    }

    @Override
    public void notify(String id, String eventType, Object payload) {
        ProducerRecord<String, CommandState> record = new ProducerRecord<>(_topic, id, new CommandState(id, eventType, payload));
        _producer.send(record);
    }

    @Override
    public void notify(String id, String eventType) {
        notify(id, eventType, null);
    }
}
