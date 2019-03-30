package com.fabiogouw.eventprocessinglib.core.ports;

import com.fabiogouw.eventprocessinglib.core.dtos.CustomEvent;

public interface EventHandler {
    String getType();
    int getLowestVersion();
    int getHighestVersion();
    void handle(CustomEvent event);
}
