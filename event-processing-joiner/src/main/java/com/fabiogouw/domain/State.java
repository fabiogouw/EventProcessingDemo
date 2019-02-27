package com.fabiogouw.domain;

public class State {
    private String _id;
    private String _value;
    private int _partition;
    private long _offset;

    public State() {
    }

    public State(String id, String value, int partition, long offset) {
        _id = id;
        _value = value;
        _partition = partition;
        _offset = offset;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        _id = id;
    }

    public String getValue() {
        return _value;
    }

    public void setValue(String value) {
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
}
