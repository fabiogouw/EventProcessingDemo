package com.fabiogouw.eventprocessingapp.adapters.controllers;

import com.fabiogouw.eventprocessingapp.ports.Holder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AsyncHolderImpl<T> implements Holder<T> {

    private final Map<String, DeferredResult<T>> _map = new ConcurrentHashMap<>();
    private static final Logger _log = LoggerFactory.getLogger(AsyncHolderImpl.class);

    @Override
    public void hold(String id, DeferredResult<T> result) {
        result.onTimeout(() -> {
            _map.remove(id);
            _log.debug("Removed by timeout: " + id);
        });
        result.onCompletion(() -> {
            _map.remove(id);
            _log.debug("Removed: " + id);
        });
        _log.debug("Added: " + id);
        _map.put(id, result);
    }

    @Override
    public void release(String id, T data) {
        DeferredResult<T> hold = _map.get(id);
        if(hold != null) {
            hold.setResult(data);
            _log.debug("Completed: " + id);
        }
    }
}
