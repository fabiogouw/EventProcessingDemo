package com.fabiogouw.eventprocessingdemo.adapters.dtos;

public class CustomEvent {
    private String _type;
    private Object _payload;

    public String getType() {
        return _type;
    }

    public void setType(String value) {
        _type = value;
    }

    public Object getPayload() {
        return _payload;
    }

    public void setPayload(Object value) {
        _payload = value;
    }

    public CustomEvent() {

    }

    public CustomEvent(String type, Object payload) {
        _type = type;
        _payload = payload;
    }
}
