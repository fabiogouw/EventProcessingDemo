package com.fabiogouw.adapters;

import com.fabiogouw.domain.valueObjects.CommandState;
import com.fabiogouw.domain.ports.JoinManager;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class KafkaRewindablePartitionProcessor {

    private final Logger _logger = LoggerFactory.getLogger(KafkaRewindablePartitionProcessor.class);
    private final org.apache.kafka.clients.consumer.Consumer<String, CommandState> _consumer;
    private final TopicPartition _partition;
    private final Consumer<CommandState> _run;
    private long _currentOffset = -1;
    private boolean _isMarkedToRewind = false;
    private long _offsetToRewind = JoinManager.BEGGINING_OFFSET;

    public int getPartitionId() {
        return _partition.partition();
    }

    public TopicPartition getTopicPartition() {
        return _partition;
    }

    public KafkaRewindablePartitionProcessor(org.apache.kafka.clients.consumer.Consumer<String, CommandState> consumer, TopicPartition partition, Consumer<CommandState> run) {
        _consumer = consumer;
        _partition = partition;
        _run = run;
    }

    public void setCurrentOffset(long offset) {
        _currentOffset = offset;
    }

    public long getCurrentOffset() {
        return _currentOffset;
    }

    public void markOffsetToRewind(long offsetToGo, long triggerOffset) {
        _isMarkedToRewind = true;
        _offsetToRewind = offsetToGo;
    }

    public void setConsumerPosition() {
        _isMarkedToRewind = false;
        _currentOffset = _consumer.position(_partition);
    }

    public boolean processRecords(List<ConsumerRecord<String, CommandState>> records) {
        for(ConsumerRecord<String, CommandState> record : records) {
            CommandState commandState = record.value();
            _logger.debug("Processing state partition: {} offset: {}...", record.partition(),  record.offset());
            try {
                _run.accept(commandState.augment(record.partition(), record.offset()));
            }
            catch(Exception ex) {
                _logger.error("Error while processing record on partition {}, offset {}. Details: {}", record.partition(), record.offset(), ex);
            }
            if(_isMarkedToRewind) {
                if(_offsetToRewind < 0) {
                    _consumer.seekToBeginning(Arrays.asList(_partition));
                }
                else {
                    _consumer.seek(_partition, _offsetToRewind);
                }
                _logger.info("KafkaRewindableEventSource - Partition {} rewinded to {}.", _partition,  _offsetToRewind);
                return false;
            }
        }
        return true;
    }
}
