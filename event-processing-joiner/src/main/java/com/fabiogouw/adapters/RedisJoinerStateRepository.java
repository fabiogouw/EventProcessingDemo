package com.fabiogouw.adapters;

import com.fabiogouw.domain.Join;
import com.fabiogouw.ports.JoinerStateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.Set;

public class RedisJoinerStateRepository implements JoinerStateRepository {

    private final Logger _logger = LoggerFactory.getLogger(RedisJoinerStateRepository.class);

    private static String PARTITION_PREFIX = "__PJOIN_";
    private static String JOIN_PREFIX = "__JOIN_";
    private final Jedis _jedis = new Jedis("172.17.0.2");

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
    public Join get(String id) {
        Set<String> states = _jedis.smembers(JOIN_PREFIX + id);
        Join join = new Join(id, states);
        return join;
    }

    @Override
    public void save(Join join, int partition, long offset) {
        String key = JOIN_PREFIX + join.getId();
        for(String state : join.getStates()) {
            _jedis.sadd(key, state);
        }
        _jedis.expire(key, 60);
        // last thing to do is to update the partition's offset, don't care
        // if the last instructions will get repeated eventually
        _logger.debug("Setting offset info for partition {}: {}.", partition, offset);
        _jedis.set(PARTITION_PREFIX + partition, String.valueOf(offset));
    }
}
