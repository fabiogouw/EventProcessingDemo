package com.fabiogouw.adapters;

import com.fabiogouw.domain.entities.Join;
import com.fabiogouw.domain.valueObjects.CommandState;
import com.fabiogouw.domain.valueObjects.EventState;
import com.fabiogouw.eventprocessinglib.core.ports.EventHandler;
import com.fabiogouw.domain.ports.JoinManager;
import com.fabiogouw.domain.ports.JoinStateRepository;
import com.fabiogouw.domain.ports.RewindableEventSource;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JoinManagerImpl implements JoinManager {
    private final JoinStateRepository _repository;
    private final RewindableEventSource _eventSource;
    private StateMachine<String, String> _stateMachine;
    private StateMachinePersister<String, String, String> _persister;

    private final Logger _logger = LoggerFactory.getLogger(JoinManagerImpl.class);

    public JoinManagerImpl(StateMachine<String, String> stateMachine,
                           JoinStateRepository repository,
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
                StateMachine<String, String> stateMachine = getStateMachine(commandState.getId());
                stateMachine.getExtendedState().getVariables().put("commandState", commandState);
                stateMachine.sendEvent(commandState.getEventType());
                _persister.persist(stateMachine, commandState.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(currentStateOffset < commandState.getOffset()) {
                _repository.setOffsetForPartition(commandState.getPartition(), commandState.getOffset());
            }
            _eventSource.setProcessedOffset(commandState.getPartition(), commandState.getOffset());
        }
    }

    private StateMachine<String, String> getStateMachine(String machineId) throws Exception {
        _stateMachine = _persister.restore(_stateMachine, machineId);
        return _stateMachine;
    }
}
