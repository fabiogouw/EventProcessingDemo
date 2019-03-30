package com.fabiogouw.eventprocessingapp.core.ports;

import org.springframework.web.context.request.async.DeferredResult;

public interface Holder<T> {
    void hold(String id, DeferredResult<T> result); // didn't like it, I'm exposing framework details here
    void release(String id, T data);
}
