package com.fabiogouw.eventprocessingdemo.adapters.handlers;

import com.fabiogouw.eventprocessingdemo.adapters.dtos.CustomEvent;

public interface EventHandler {
    String getType();
    Boolean handle(CustomEvent event);
}
