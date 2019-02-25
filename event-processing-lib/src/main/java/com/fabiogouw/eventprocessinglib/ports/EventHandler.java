package com.fabiogouw.eventprocessinglib.ports;

import com.fabiogouw.eventprocessinglib.dtos.CustomEvent;

public interface EventHandler {
    String getType();
    int getLowestVersion();
    int getHighestVersion();
    void handle(CustomEvent event);
}
