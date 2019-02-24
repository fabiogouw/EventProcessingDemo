package com.fabiogouw.eventprocessingdemo.adapters.dtos;

public class CustomEvent {
    private String _type;
    private int _version;
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

    public int getVersion() {
        return _version;
    }

    public CustomEvent() {

    }

    public CustomEvent(String type, int version, Object payload) {
        _type = type;
        _version = version;
        _payload = payload;
    }
}
