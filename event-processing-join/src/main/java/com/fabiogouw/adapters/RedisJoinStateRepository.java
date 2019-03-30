package com.fabiogouw.adapters;

import com.fabiogouw.domain.entities.Join;
import com.fabiogouw.domain.valueObjects.EventState;
import com.fabiogouw.domain.ports.JoinStateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.Map;
import java.util.stream.Collectors;

public class RedisJoinStateRepository implements JoinStateRepository {

    private final Logger _logger = LoggerFactory.getLogger(RedisJoinStateRepository.class);

    private static String PARTITION_PREFIX = "__PJOIN_";
    private static String JOIN_PREFIX = "__JOIN_";
    private final Jedis _jedis;
    private final int _expirationInSeconds;

    public RedisJoinStateRepository(Jedis jedis, int expirationInSeconds) {
        _jedis = jedis;
        _expirationInSeconds = expirationInSeconds;
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
    public Join get(String id) {
        Map<String, String> states = _jedis.hgetAll(JOIN_PREFIX + id);
        Join join = new Join(id, states.entrySet().stream()
                .map(s -> new EventState(s.getKey(), s.getValue()))
                .collect(Collectors.toSet()));
        _logger.debug("Getting join info: Id: {}, States: '{}'.", join.getId(),
                String.join(", ", join.getStates().stream().map(es -> es.getEvent()).collect(Collectors.toList())));
        return join;
    }

    @Override
    public void save(Join join, int partition, long offset) {
        String key = JOIN_PREFIX + join.getId();
        for(EventState eventState : join.getStates()) {
            _jedis.hset(key, eventState.getEvent(), eventState.getValue());
        }
        _jedis.expire(key, _expirationInSeconds);
        // last thing to do is to update the partition's offset, don't care
        // if the last instructions will get repeated eventually
        _logger.debug("Saving join info: Id: {}, States: '{}'.", join.getId(),
                String.join(", ", join.getStates().stream().map(es -> es.getEvent()).collect(Collectors.toList())));
        _logger.debug("Setting offset info for partition {}: {}.", partition, offset);
        _jedis.set(PARTITION_PREFIX + partition, String.valueOf(offset));
    }
}
