package com.fabiogouw.eventprocessingdemo.ports;

import com.fabiogouw.eventprocessingdemo.adapters.dtos.CustomEvent;

import java.util.function.Consumer;

public interface EventSource {
    void subscribe(Consumer<CustomEvent> run);
}
