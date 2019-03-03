package com.fabiogouw.domain.entities;

import com.fabiogouw.domain.valueObjects.EventState;

import java.util.Set;

public class Join {
    private String _id;
    private Set<EventState> _eventStates;

    public String getId() {
        return _id;
    }

    public Set<EventState> getStates() {
        return _eventStates;
    }

    public Join(String id) {
        _id = id;
    }

    public Join(String id, Set<EventState> eventStates) {
        this(id);
        _eventStates = eventStates;
    }

    public void addState(EventState eventState) {
        if(!_eventStates.contains(eventState)) {
            _eventStates.add(eventState);
        }
    }
}
