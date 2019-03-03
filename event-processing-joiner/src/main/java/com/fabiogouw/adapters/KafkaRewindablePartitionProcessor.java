package com.fabiogouw.adapters;

import com.fabiogouw.domain.State;
import com.fabiogouw.ports.Joiner;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class KafkaRewindablePartitionProcessor {

    private final Logger _logger = LoggerFactory.getLogger(KafkaRewindablePartitionProcessor.class);
    private final org.apache.kafka.clients.consumer.Consumer<String, State> _consumer;
    private final TopicPartition _partition;
    private final Consumer<State> _run;
    private long _currentOffset = -1;
    private boolean _isMarkedToRewind = false;
    private long _offsetToRewind = Joiner.BEGGINING_OFFSET;

    public int getPartitionId() {
        return _partition.partition();
    }

    public TopicPartition getTopicPartition() {
        return _partition;
    }

    public KafkaRewindablePartitionProcessor(org.apache.kafka.clients.consumer.Consumer<String, State> consumer, TopicPartition partition, Consumer<State> run) {
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

    public boolean processRecords(List<ConsumerRecord<String, State>> records) {
        for(ConsumerRecord<String, State> record : records) {
            State state = record.value();
            state.setOffset(record.offset());
            state.setPartition(record.partition());
            _logger.debug("Processing state partition: {} offset: {}...", state.getPartition(),  state.getOffset());
            try {
                _run.accept(state);
            }
            catch(Exception ex) {
                _logger.error("Error while processing record on partition {}, offset {}. Details: {}", state.getPartition(), state.getOffset(), ex);
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
