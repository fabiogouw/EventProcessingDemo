package com.fabiogouw.eventprocessingapp.adapters.controllers;

import com.fabiogouw.eventprocessingapp.adapters.dtos.Withdraw;
import com.fabiogouw.eventprocessingapp.adapters.sources.WebEventSource;
import com.fabiogouw.eventprocessingapp.ports.Holder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.UUID;

@RestController
@RequestMapping(value = "/withdraw")
public class WithdrawController {
    private final Logger _logger = LoggerFactory.getLogger(WithdrawController.class);

    private final WebEventSource<Withdraw> _withdrawEventSource;
    private final Holder<Withdraw> _holder;

    public WithdrawController(WebEventSource<Withdraw> withdrawEventSource, Holder<Withdraw> holder) {
        _withdrawEventSource = withdrawEventSource;
        _holder = holder;
    }

    @PostMapping(value = "/")
    public @ResponseBody DeferredResult<Withdraw> create(@RequestBody Withdraw withdraw) {
        if(withdraw.getCorrelationId() == null || withdraw.getCorrelationId().isEmpty()) {
            withdraw.setCorrelationId(UUID.randomUUID().toString());
        }
        DeferredResult<Withdraw> result = new DeferredResult<>(5000l);
        _holder.hold(withdraw.getCorrelationId(), result);
        _withdrawEventSource.send(withdraw.getCorrelationId(), "com.fabiogouw.eventprocessingdemo.WithdrawRequest", 1, withdraw);
        return result;
    }
}
