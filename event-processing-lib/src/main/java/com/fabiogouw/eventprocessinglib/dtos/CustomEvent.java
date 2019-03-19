package com.fabiogouw.eventprocessinglib.dtos;

import java.util.function.Supplier;

public class CustomEvent {

    public final static String CORRELATION_ID = "correlation_id";
    public final static String EVENT_TYPE = "event_type";
    public final static String EVENT_TYPE_VERSION = "event_type_version";

    private String _correlationId;
    private String _type;
    private int _version;
    private Object _payload;
    private Supplier<Object> _payloadExtractor;

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

    public int getVersion() {
        return _version;
    }

    public void setVersion(int value) {
        _version = value;
    }

    public Object getPayload() {
        if(_payload == null && _payloadExtractor != null) {
            _payload = _payloadExtractor.get();
        }
        return _payload;
    }

    public void setPayload(Object value) {
        _payload = value;
    }

    public CustomEvent() {

    }

    public CustomEvent(String correlationId, String type, int version, Supplier<Object> payloadExtractor) {
        _correlationId = correlationId;
        _type = type;
        _version = version;
        _payloadExtractor = payloadExtractor;
    }
}
