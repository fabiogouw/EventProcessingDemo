package com.fabiogouw.adapters;

import com.fabiogouw.domain.ports.StateControlRepository;
import com.fabiogouw.domain.valueObjects.CommandState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.persist.StateMachinePersister;

import java.util.Map;

public class RedisCommandSourcedStateMachinePersister<S, E, T> implements StateMachinePersister<S, E, T> {

    private final Logger _logger = LoggerFactory.getLogger(RedisCommandSourcedStateMachinePersister.class);

    private final StateMachinePersister<S, E, T> _persister;
    private final StateControlRepository _stateRepository;

    public RedisCommandSourcedStateMachinePersister(StateMachinePersister<S, E, T> persister,
                                                    StateControlRepository stateRepository) {
        _persister = persister;
        _stateRepository = stateRepository;
    }

    @Override
    public void persist(StateMachine<S, E> stateMachine, T t) throws Exception {
        final ExtendedState extendedState = stateMachine.getExtendedState();
        final Map<Object, Object> variables = extendedState.getVariables();
        CommandState commandState = (CommandState) variables.getOrDefault("commandState", new CommandState());
        _persister.persist(stateMachine, t);
        long currentStateOffset = _stateRepository.getOffsetForPartition(commandState.getPartition());
        if(currentStateOffset < commandState.getOffset()) {
            _stateRepository.setOffsetForPartition(commandState.getPartition(), commandState.getOffset());
        }
    }

    @Override
    public StateMachine<S, E> restore(StateMachine<S, E> stateMachine, T t) throws Exception {
        return _persister.restore(stateMachine, t);
    }
}
