package com.fabiogouw.ports;

import com.fabiogouw.domain.Join;

public interface JoinerStateRepository {
    long getOffsetForPartition(int partition);
    Join get(String id);
    void save(Join join, int partition, long offset);
}
