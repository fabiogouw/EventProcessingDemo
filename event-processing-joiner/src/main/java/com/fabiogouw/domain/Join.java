package com.fabiogouw.domain;

import java.util.Set;

public class Join {
    private String _id;
    private Set<String> _states;

    public String getId() {
        return _id;
    }

    public Set<String> getStates() {
        return _states;
    }

    public Join(String id) {
        _id = id;
    }

    public Join(String id, Set<String> states) {
        this(id);
        _states = states;
    }

    public void addState(String state) {
        if(!_states.contains(state)) {
            _states.add(state);
        }
    }
}
