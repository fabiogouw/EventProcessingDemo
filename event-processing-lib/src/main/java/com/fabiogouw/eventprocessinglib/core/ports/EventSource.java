package com.fabiogouw.eventprocessinglib.core.ports;

import com.fabiogouw.eventprocessinglib.core.dtos.CustomEvent;

import java.util.function.Consumer;

public interface EventSource {
    void subscribe(Consumer<CustomEvent> run);
    void unsubscribe();
}
