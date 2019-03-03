package com.fabiogouw.domain.valueObjects;

public class CommandState {
    private String _id;
    private String _eventType;
    private Object _value;
    private int _partition;
    private long _offset;

    public CommandState() {

    }

    public CommandState(String id, String eventType, Object value) {
        this(id, eventType, value, -1, -1);
    }

    public CommandState(String id, String eventType, Object value, int partition, long offset) {
        _id = id;
        _eventType = eventType;
        _value = value;
        _partition = partition;
        _offset = offset;
    }

    public String getId() {
        return _id;
    }

    public void setId(String value) {
        _id = value;
    }

    public String getEventType() {
        return _eventType;
    }

    public void setEventType(String value) {
        _eventType = value;
    }

    public Object getValue() {
        return _value;
    }

    public void setValue(Object value) {
        _value = value;
    }

    public int getPartition() {
        return _partition;
    }

    public void setPartition(int value) {
        _partition = value;
    }

    public long getOffset() {
        return _offset;
    }

    public void setOffset(long value) {
        _offset = value;
    }

    public CommandState augment(int partition, long offset) {
        return new CommandState(_id, _eventType, _value, partition, offset);
    }
}
