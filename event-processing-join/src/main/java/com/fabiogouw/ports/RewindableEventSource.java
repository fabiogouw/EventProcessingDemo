package com.fabiogouw.ports;

import com.fabiogouw.domain.valueObjects.CommandState;

import java.util.function.Consumer;

public interface RewindableEventSource {
    void subscribe(Consumer<CommandState> run);
    void setProcessedOffset(int partition, long offset);
    void rewindTo(int partition, long offsetToGo, long triggerOffset);
    void unsubscribe();
}
