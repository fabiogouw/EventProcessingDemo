package com.fabiogouw.ports;

import com.fabiogouw.domain.State;

import java.util.function.Consumer;

public interface RewindableEventSource {
    void subscribe(Consumer<State> run);
    void rewindTo(int partition, long offset);
    void unsubscribe();
}
