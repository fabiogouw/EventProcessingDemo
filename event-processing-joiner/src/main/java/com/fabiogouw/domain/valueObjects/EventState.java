package com.fabiogouw.domain.valueObjects;

public class EventState {
    private String _event;
    private String _value;

    public String getEvent() {
        return _event;
    }

    public void setEvent(String value) {
        _event = _event;
    }

    public String getValue() {
        return _value;
    }

    public void setValue(String value) {
        _value = value;
    }

    public EventState() {

    }

    public EventState(String event, String value) {
        _event = event;
        _value = value;
    }
}
