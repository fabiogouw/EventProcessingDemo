package com.fabiogouw.adapters;

import com.fabiogouw.domain.ports.JoinStateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.data.redis.RedisStateMachinePersister;
import org.springframework.statemachine.persist.AbstractPersistingStateMachineInterceptor;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.support.StateMachineInterceptor;
import redis.clients.jedis.Jedis;

public class RedisCommandSourceStateMachineInterceptor<S, E, T> extends AbstractPersistingStateMachineInterceptor<S, E, T>
        implements StateMachineRuntimePersister<S, E, T> {

    private static String PARTITION_PREFIX = "__PJOIN_";
    private final Logger _logger = LoggerFactory.getLogger(RedisCommandSourceStateMachineInterceptor.class);

    private StateMachineRuntimePersister<S, E, T> _redisInterceptor;
    private final JoinStateRepository _stateRepository;
    private final Jedis _jedis;

    public RedisCommandSourceStateMachineInterceptor(StateMachineRuntimePersister<S, E, T> redisInterceptor,
                                                     JoinStateRepository stateRepository,
                                                     Jedis jedis) {
        _redisInterceptor = redisInterceptor;
        _stateRepository = stateRepository;
        _jedis = jedis;
    }

    @Override
    public void write(StateMachineContext<S, E> stateMachineContext, T t) throws Exception {
        // TODO: handle when there's no such variables
        int partition = Integer.parseInt(stateMachineContext.getExtendedState().getVariables().get("partition").toString());
        long offset = Long.parseLong(stateMachineContext.getExtendedState().getVariables().get("offset").toString());
        _redisInterceptor.write(stateMachineContext, t);
        _stateRepository.setOffsetForPartition(partition, offset);
    }

    @Override
    public StateMachineContext<S, E> read(T t) throws Exception {
        return _redisInterceptor.read(t);
    }

    @Override
    public StateMachineInterceptor<S, E> getInterceptor() {
        return this;
    }
}
