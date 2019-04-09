package com.fabiogouw.adapters;

import com.fabiogouw.domain.ports.StateControlRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

public class RedisControlStateRepository implements StateControlRepository {

    private final Logger _logger = LoggerFactory.getLogger(RedisControlStateRepository.class);

    private static String PARTITION_PREFIX = "__PJOIN_";
    private final Jedis _jedis;

    public RedisControlStateRepository(Jedis jedis) {
        _jedis = jedis;
    }

    @Override
    public long getOffsetForPartition(int partition) {
        long currentOffset = -1;
        String value =_jedis.get(PARTITION_PREFIX + partition);
        if(value != null && !value.isEmpty()) {
            currentOffset = Integer.parseInt(value);
        }
        _logger.debug("Getting offset info for partition {}: {}.", partition, currentOffset);
        return currentOffset;
    }

    @Override
    public void setOffsetForPartition(int partition, long offset) {
        _logger.debug("Setting offset info for partition {}: {}.", partition, offset);
        _jedis.set(PARTITION_PREFIX + partition, String.valueOf(offset));
    }
}
