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

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JoinManagerImpl implements JoinManager {
    private List<String> _expectedStates;
    private Consumer<Join> _onCompletion;
    private final JoinStateRepository _repository;
    private final RewindableEventSource _eventSource;
    private Predicate<Set<EventState>> _checkJoin;
    private final ObjectMapper _objectMapper = new ObjectMapper();

    private final Logger _logger = LoggerFactory.getLogger(JoinManagerImpl.class);

    public JoinManagerImpl(EventHandler[] handlers, JoinStateRepository repository, RewindableEventSource eventSource) {
        this(Stream.of(handlers).map(h -> h.getType()).collect(Collectors.toList()), repository, eventSource);
    }

    public JoinManagerImpl(List<String> expectedStates, JoinStateRepository repository, RewindableEventSource eventSource) {
        _expectedStates = expectedStates;
        _repository = repository;
        _eventSource = eventSource;
        _checkJoin = this::checkJoin;   //we might want to change the default state validation
    }

    public void setBehavior(Consumer<Join> onCompletion) {
        _eventSource.unsubscribe();
        _onCompletion = onCompletion;
        _eventSource.subscribe(this::addState);
        _logger.info("Join listening to '{}'...", String.join(", ", _expectedStates));
    }

    public void stop() {
        _eventSource.unsubscribe();
    }

    private void addState(CommandState commandState) {
        long currentStateOffset = _repository.getOffsetForPartition(commandState.getPartition());
        if(currentStateOffset < commandState.getOffset() - 1) {
            _logger.warn("Something is strange. While processing the partition {}, the current offset should be {}, but it was {}.", commandState.getPartition(), commandState.getOffset() - 1, currentStateOffset);
            long offsetToGo = currentStateOffset < 0 ? JoinManager.BEGGINING_OFFSET : currentStateOffset;
            _eventSource.rewindTo(commandState.getPartition(), offsetToGo, commandState.getOffset());
        }
        else {
            Join join = _repository.get(commandState.getId());
            EventState state = getEventStateObject(commandState);
            join.addState(state);
            if(currentStateOffset + 1 == commandState.getOffset()) {
                boolean shouldComplete = testJoin(join.getStates());
                if(shouldComplete) {
                    try {
                        _onCompletion.accept(join);
                    }
                    catch(Exception ex) {
                        _logger.error("Error while processing join on partition {}, offset {}. Details: {}", commandState.getPartition(), commandState.getOffset(), ex);
                    }
                }
            }
            if(currentStateOffset < commandState.getOffset()) {
                _repository.save(join, commandState.getPartition(), commandState.getOffset());
            }
            _eventSource.setProcessedOffset(commandState.getPartition(), commandState.getOffset());
        }
    }

    private boolean testJoin(Set<EventState> states) {
        try {
            return _checkJoin.test(states);
        }
        catch (Exception ex) {
            _logger.error("Error while checking join: {}", ex.toString());
            return false;
        }
    }

    private EventState getEventStateObject(CommandState commandState) {
        EventState state = null;
        try {
            state = new EventState(commandState.getEventType(), _objectMapper.writeValueAsString(commandState.getValue()));
        } catch (JsonProcessingException ex) {
            _logger.error("Error while storing event data for partition {} and {}: {}", commandState.getPartition(), commandState.getOffset(),  ex);
        }
        return state;
    }

    private boolean checkJoin(Set<EventState> currentEventStates) {
        _logger.debug("Checking join: Expected: {}. Current: {}",
                String.join(", ", _expectedStates),
                String.join(", ", currentEventStates.stream().map(es -> es.getEvent()).collect(Collectors.toList())));
        int expectedCount = _expectedStates.size();
        for(EventState eventState : currentEventStates) {
            if(_expectedStates.contains(eventState.getEvent())) {
                expectedCount--;
            }
        }
        return expectedCount == 0;
    }

}
