package com.fabiogouw.domain.ports;

import com.fabiogouw.domain.entities.Join;

public interface JoinStateRepository {
    long getOffsetForPartition(int partition);
    Join get(String id);
    void save(Join join, int partition, long offset);
}
