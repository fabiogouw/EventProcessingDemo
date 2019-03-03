package com.fabiogouw.ports;

import java.util.List;
import java.util.function.Consumer;

public interface JoinManager {
    long BEGGINING_OFFSET = -1;
    void setBehavior(List<String> expectedStates, Consumer<String> onCompletion);
    void stop();
}
