package com.fabiogouw.domain.ports;

public interface JoinNotifier {
    void notify(String id, String eventType, Object payload);
    void notify(String id, String eventType);
}
