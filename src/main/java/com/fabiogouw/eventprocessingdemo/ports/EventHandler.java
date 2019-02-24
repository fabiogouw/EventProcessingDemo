package com.fabiogouw.eventprocessingdemo.ports;

import com.fabiogouw.eventprocessingdemo.adapters.dtos.CustomEvent;

public interface EventHandler {
    String getType();
    int getLowestVersion();
    int getHighestVersion();
    void handle(CustomEvent event);
}
