package com.fabiogouw.domain.ports;

public interface JoinStateRepository {
    long getOffsetForPartition(int partition);
    void setOffsetForPartition(int partition, long offset);
}
