package com.fabiogouw.domain;

import com.fabiogouw.ports.Joiner;
import com.fabiogouw.ports.JoinerStateRepository;
import com.fabiogouw.ports.RewindableEventSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class JoinerImpl implements Joiner {
    private List<String> _expectedStates;
    private Consumer<String> _onCompletion;
    private final JoinerStateRepository _repository;
    private final RewindableEventSource _eventSource;
    private Predicate<Set<String>> _checkJoin;

    private final Logger _logger = LoggerFactory.getLogger(JoinerImpl.class);

    public JoinerImpl(JoinerStateRepository repository, RewindableEventSource eventSource) {
        _repository = repository;
        _eventSource = eventSource;
        _checkJoin = this::checkJoin;   //we might want to change the default state validation
    }

    public void setBehavior(List<String> expectedStates, Consumer<String> onCompletion) {
        _eventSource.unsubscribe();
        _expectedStates = expectedStates;
        _onCompletion = onCompletion;
        _eventSource.subscribe(this::addState);
    }

    public void stop() {
        _eventSource.unsubscribe();
    }

    private void addState(State state) {
        long currentStateOffset = _repository.getOffsetForPartition(state.getPartition());
        if(currentStateOffset < state.getOffset() - 1) {
            _logger.warn("Something is strange. While processing the partition {}, the current offset should be {}, but it was {}.", state.getPartition(), state.getOffset() - 1, currentStateOffset);
            long offsetToGo = currentStateOffset < 0 ? Joiner.BEGGINING_OFFSET : currentStateOffset;
            _eventSource.rewindTo(state.getPartition(), offsetToGo, state.getOffset());
        }
        else {
            Join join = _repository.get(state.getId());
            join.addState(state.getValue());
            if(currentStateOffset + 1 == state.getOffset()) {
                boolean shouldComplete = _checkJoin.test(join.getStates());
                if(shouldComplete) {
                    try {
                        _onCompletion.accept(join.getId());
                    }
                    catch(Exception ex) {
                        _logger.error("Error while processing join on partition {}, offset {}. Details: {}", state.getPartition(), state.getOffset(), ex);
                    }
                }
            }
            if(currentStateOffset < state.getOffset()) {
                _repository.save(join, state.getPartition(), state.getOffset());
            }
            _eventSource.setProcessedOffset(state.getPartition(), state.getOffset());
        }
    }

    private boolean checkJoin(Set<String> currentStates) {
        int expectedCount = _expectedStates.size();
        for(String state : _expectedStates) {
            if(currentStates.contains(state)) {
                expectedCount--;
            }
        }
        return expectedCount == 0;
    }

}
