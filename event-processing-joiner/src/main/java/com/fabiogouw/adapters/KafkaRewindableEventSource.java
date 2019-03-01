package com.fabiogouw.adapters;

import com.fabiogouw.domain.State;
import com.fabiogouw.ports.RewindableEventSource;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class KafkaRewindableEventSource implements RewindableEventSource {

    private final Logger _logger = LoggerFactory.getLogger(KafkaRewindableEventSource.class);
    private org.apache.kafka.clients.consumer.Consumer<String, State> _consumer;
    private volatile boolean _running = false;
    private volatile Map<Integer, Long> _offsets = new HashMap<>();
    private Consumer<State> _run;

    public KafkaRewindableEventSource(org.apache.kafka.clients.consumer.Consumer<String, State> consumer) {
        _consumer = consumer;
    }

    @Override
    public void subscribe(Consumer<State> run) {
        _run = run;
        _logger.info("KafkaRewindableEventSource subscribed.");
        _running = true;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        Runnable runnableTask = () -> consume();
        executor.execute(runnableTask);
    }

    @Override
    public void rewindTo(int partition, long offset) {
        _logger.info("KafkaRewindableEventSource rewind to {}.", offset);
        _offsets.replace(partition, offset);
    }

    @Override
    public void unsubscribe() {
        _logger.info("KafkaRewindableEventSource unsubscribed.");
        _running = false;
    }

    private void consume() {
        try {
            _logger.info("KafkaRewindableEventSource is starting to consume...");
            while(_running) {
                Map<TopicPartition, OffsetAndMetadata> commitMap = new HashMap<>();
                ConsumerRecords<String, State> consumerRecords = _consumer.poll(Duration.ofMillis(500));
                Set<TopicPartition> partitions = consumerRecords.partitions();
                for(TopicPartition partition : partitions) {
                    long offset = -1;
                    List<ConsumerRecord<String, State>> recordsPerPartition = consumerRecords.records(partition);
                    for(ConsumerRecord<String, State> record : recordsPerPartition) {
                        _run.accept(record.value());
                        long currentOffset = _offsets.getOrDefault(record.partition(), -1l);
                        if(currentOffset != -1 && currentOffset < record.offset()) {
                            _consumer.seek(partition, currentOffset);
                            offset = -1;
                            break;
                        }
                        else {
                            _offsets.replace(record.partition(), -1l);
                            offset = record.offset();
                        }
                    }
                    if(offset > -1) {
                        commitMap.put(partition, new OffsetAndMetadata(offset));
                    }
                }
                _consumer.commitSync(commitMap);
            }
        }
        finally {
            _consumer.close();
            _logger.info("KafkaRewindableEventSource closed.");
        }
    }
}
