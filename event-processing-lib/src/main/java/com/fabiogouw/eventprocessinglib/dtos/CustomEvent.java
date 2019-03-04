package com.fabiogouw.eventprocessinglib.dtos;

public class CustomEvent {
    private String _correlationId;
    private String _type;
    private int _version;
    private Object _payload;

    public String getCorrelationId() {
        return _correlationId;
    }

    public void setCorrelationId(String value) {
        _correlationId = value;
    }

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

    public void setVersion(int value) {
        _version = value;
    }

    public CustomEvent() {

    }

    public CustomEvent(String correlationId, String type, int version, Object payload) {
        _correlationId = correlationId;
        _type = type;
        _version = version;
        _payload = payload;
    }
}
