package com.fabiogouw.adapters;

import com.fabiogouw.domain.Join;
import com.fabiogouw.ports.JoinerStateRepository;
import redis.clients.jedis.Jedis;

import java.util.Set;

public class RedisJoinerStateRepository implements JoinerStateRepository {

    private static String PARTITION_PREFIX = "__PJOIN_";
    private static String JOIN_PREFIX = "__JOIN_";
    private final Jedis _jedis = new Jedis("172.17.0.2");

    @Override
    public long getOffsetForPartition(int partition) {
        String value =_jedis.get(PARTITION_PREFIX + partition);
        if(value != null && !value.isEmpty()) {
            return Integer.parseInt(value);
        }
        return -1;
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
        _jedis.set(PARTITION_PREFIX + partition, String.valueOf(offset));
    }
}
