package com.fabiogouw.adapters;

import com.fabiogouw.domain.ports.StateControlRepository;
import com.fabiogouw.domain.valueObjects.CommandState;
import com.fabiogouw.domain.ports.ReactiveStateMachineManager;
import com.fabiogouw.domain.ports.RewindableEventSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.persist.StateMachinePersister;

import java.util.Collection;

public class ReactiveStateMachineManagerImpl implements ReactiveStateMachineManager {
    private final StateControlRepository _repository;
    private final RewindableEventSource _eventSource;
    private StateMachine<String, String> _stateMachine;
    private StateMachinePersister<String, String, String> _persister;

    private final Logger _logger = LoggerFactory.getLogger(ReactiveStateMachineManagerImpl.class);

    public ReactiveStateMachineManagerImpl(StateMachine<String, String> stateMachine,
                                           StateControlRepository repository,
                                           RewindableEventSource eventSource,
                                           StateMachinePersister<String, String, String> persister) {
        _stateMachine = stateMachine;
        _repository = repository;
        _eventSource = eventSource;
        _persister = persister;
    }

    public void start() {
        _eventSource.subscribe(this::addState);
        _logger.info("State machine listening to ...");
    }

    public void stop() {
        _eventSource.unsubscribe();
    }

    private synchronized void addState(CommandState commandState) {
        long currentStateOffset = _repository.getOffsetForPartition(commandState.getPartition());
        if(currentStateOffset < commandState.getOffset() - 1) {
            _logger.warn("Something is strange. While processing the partition {}, the current offset should be {}, but it was {}.", commandState.getPartition(), commandState.getOffset() - 1, currentStateOffset);
            long offsetToGo = currentStateOffset < 0 ? -1 : currentStateOffset;
            _eventSource.rewindTo(commandState.getPartition(), offsetToGo, commandState.getOffset());
        }
        else {
            try {
                String id = "__SSM:" + commandState.getId();
                StateMachine<String, String> stateMachine = getStateMachine(id);
                final Collection<String> statesBefore = stateMachine.getState().getIds();
                stateMachine.getExtendedState().getVariables().put("commandState", commandState);
                stateMachine.sendEvent(commandState.getEventType());
                _logger.debug("State machine {}: BEFORE: {} : '{}' : AFTER {}",  id, statesBefore, commandState.getEventType(), stateMachine.getState().getIds());
                _persister.persist(stateMachine, id);
            } catch (Exception ex) {
                _logger.error("Error while processing state: {}", ex);
            }
            _eventSource.setProcessedOffset(commandState.getPartition(), commandState.getOffset());
        }
    }

    private StateMachine<String, String> getStateMachine(String machineId) throws Exception {
        _stateMachine = _persister.restore(_stateMachine, machineId);
        return _stateMachine;
    }
}
