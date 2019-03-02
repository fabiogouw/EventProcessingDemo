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
import java.util.*;
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
    public void setProcessedOffset(int partition, long offset) {
        _offsets.put(partition, offset);
    }

    @Override
    public void rewindTo(int partition, long offset) {
        _logger.info("KafkaRewindableEventSource - Partition {} marked to rewind to {}.", partition,  offset);
        _offsets.put(partition, offset);
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
                    _offsets.put(partition.partition(), _consumer.position(partition));
                    List<ConsumerRecord<String, State>> recordsPerPartition = consumerRecords.records(partition);
                    long offset = processPartition(recordsPerPartition, partition);
                    if(offset >= 0) {
                        _logger.debug("Marking partition {} offset {} to commit.", partition.partition(), offset);
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

    private long processPartition(List<ConsumerRecord<String, State>> records, TopicPartition partition) {
        long offset = -1;
        for(ConsumerRecord<String, State> record : records) {
            State state = record.value();
            state.setOffset(record.offset());
            state.setPartition(record.partition());
            _logger.debug("Processing state partition: {} offset: {}...", state.getPartition(),  state.getOffset());
            _run.accept(state);
            long currentOffset = _offsets.getOrDefault(record.partition(), -1l);
            if(currentOffset < record.offset()) {
                if(currentOffset < 0) {
                    _consumer.seekToBeginning(Arrays.asList(partition));
                }
                else {
                    _consumer.seek(partition, currentOffset);
                }
                _logger.info("KafkaRewindableEventSource - Partition {} rewinded to {}.", partition,  currentOffset);
                break;
            }
            else {
                offset = record.offset();
            }
        }
        return offset;
    }
}
