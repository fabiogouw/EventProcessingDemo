package com.fabiogouw.adapters;

import com.fabiogouw.domain.valueObjects.CommandState;
import com.fabiogouw.domain.ports.RewindableEventSource;
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
    private org.apache.kafka.clients.consumer.Consumer<String, CommandState> _consumer;
    private volatile boolean _running = false;
    private final Map<Integer, KafkaRewindablePartitionProcessor> _partitionProcessors = new HashMap<>();
    private Consumer<CommandState> _run;

    public KafkaRewindableEventSource(org.apache.kafka.clients.consumer.Consumer<String, CommandState> consumer) {
        _consumer = consumer;
    }

    @Override
    public void subscribe(Consumer<CommandState> run) {
        _run = run;
        _logger.info("KafkaRewindableEventSource subscribed.");
        _running = true;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        Runnable runnableTask = () -> consume();
        executor.execute(runnableTask);
    }

    @Override
    public void setProcessedOffset(int partition, long offset) {
        _partitionProcessors.get(partition).setCurrentOffset(offset);
    }

    @Override
    public void rewindTo(int partition, long offsetToGo, long triggerOffset) {
        _logger.info("KafkaRewindableEventSource - Partition {} marked to rewind to {}, triggered by {}.", partition,  offsetToGo, triggerOffset);
        _partitionProcessors.get(partition).markOffsetToRewind(offsetToGo, triggerOffset);
    }

    @Override
    public void unsubscribe() {
        _logger.info("KafkaRewindableEventSource unsubscribed.");
        _running = false;
    }

    private void consume() {
        try {
            _logger.info("KafkaRewindableEventSource is starting to consume...");
            Map<TopicPartition, OffsetAndMetadata> commitMap = new HashMap<>();
            while(_running) {
                ConsumerRecords<String, CommandState> consumerRecords = _consumer.poll(Duration.ofMillis(500));
                Set<KafkaRewindablePartitionProcessor> partitionProcessors = getPartitionProcessors(consumerRecords.partitions());
                for(KafkaRewindablePartitionProcessor partitionProcessor : partitionProcessors) {
                    List<ConsumerRecord<String, CommandState>> recordsPerPartition = consumerRecords.records(partitionProcessor.getTopicPartition());
                    if(recordsPerPartition.size() > 0) {
                        partitionProcessor.setConsumerPosition();
                        boolean shouldCommit = partitionProcessor.processRecords(recordsPerPartition);
                        if(shouldCommit) {
                            _logger.debug("Marking partition {} to commit at offset {}...", partitionProcessor.getTopicPartition(), partitionProcessor.getCurrentOffset());
                            commitMap.put(partitionProcessor.getTopicPartition(), new OffsetAndMetadata(partitionProcessor.getCurrentOffset()));
                        }
                    }
                }
                if(commitMap.size() > 0) {
                    _consumer.commitSync(commitMap);
                    commitMap.clear();
                }
            }
        }
        finally {
            _consumer.close();
            _partitionProcessors.clear();
            _logger.info("KafkaRewindableEventSource closed.");
        }
    }

    private Set<KafkaRewindablePartitionProcessor> getPartitionProcessors(Set<TopicPartition> partitions) {
        for(TopicPartition partition : partitions) {
            if(!_partitionProcessors.containsKey(partition.partition())) {
                _partitionProcessors.put(partition.partition(), new KafkaRewindablePartitionProcessor(_consumer, partition, _run));
            }
        }
        return new HashSet<>(_partitionProcessors.values());
    }
}
