package com.fabiogouw.eventprocessinglib.ports;

import com.fabiogouw.eventprocessinglib.dtos.CustomEvent;

import java.util.function.Consumer;

public interface EventSource {
    void subscribe(Consumer<CustomEvent> run);
    void unsubscribe();
}
