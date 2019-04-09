package com.fabiogouw.domain.ports;

public interface StateControlRepository {
    long getOffsetForPartition(int partition);
    void setOffsetForPartition(int partition, long offset);
}
