package com.fabiogouw.adapters;

import com.fabiogouw.eventprocessinglib.core.dtos.CustomEvent;
import com.fabiogouw.eventprocessinglib.core.ports.EventHandler;
import com.fabiogouw.domain.ports.ReactiveStateMachineEventNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultJoinEventHandler implements EventHandler {

    private final ReactiveStateMachineEventNotifier _reactiveStateMachineEventNotifier;
    private final String _eventType;
    private final int _lowestVersion;
    private final int _highestVersion;
    private final Logger _logger = LoggerFactory.getLogger(DefaultJoinEventHandler.class);

    public DefaultJoinEventHandler(ReactiveStateMachineEventNotifier reactiveStateMachineEventNotifier, String eventType, int lowestVersion, int highestVersion) {
        _reactiveStateMachineEventNotifier = reactiveStateMachineEventNotifier;
        _eventType = eventType;
        _lowestVersion = lowestVersion;
        _highestVersion = highestVersion;
    }

    @Override
    public String getType() {
        return _eventType;
    }

    @Override
    public int getLowestVersion() {
        return _lowestVersion;
    }

    @Override
    public int getHighestVersion() {
        return _highestVersion;
    }

    @Override
    public void handle(CustomEvent event) {
        _logger.debug("Handling event for join of {}...", _eventType);
        _reactiveStateMachineEventNotifier.notify(event.getCorrelationId(), _eventType, event.getPayload());
    }
}
