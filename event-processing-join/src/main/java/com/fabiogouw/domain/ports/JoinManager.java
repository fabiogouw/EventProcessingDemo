package com.fabiogouw.domain.ports;

import com.fabiogouw.domain.entities.Join;

import java.util.function.Consumer;

public interface JoinManager {
    long BEGGINING_OFFSET = -1;
    void setBehavior(Consumer<Join> onCompletion);
    void stop();
}
