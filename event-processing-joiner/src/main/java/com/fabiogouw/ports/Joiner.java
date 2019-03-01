package com.fabiogouw.ports;

import java.util.List;
import java.util.function.Consumer;

public interface Joiner {
    void setBehavior(List<String> expectedStates, Consumer<String> onCompletion);
    void stop();
}
