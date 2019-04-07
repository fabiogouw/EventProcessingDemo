package com.fabiogouw.eventprocessingapp.core.statemachine;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;

@Configuration
@EnableStateMachine
public class WithdrawStateMachineConfig extends StateMachineConfigurerAdapter<String, String> {

    private final StateMachineRuntimePersister<String, String, String> _stateMachineRuntimePersister;

    public WithdrawStateMachineConfig(StateMachineRuntimePersister<String, String, String> stateMachineRuntimePersister) {
        _stateMachineRuntimePersister = stateMachineRuntimePersister;
    }

    public Action<String, String> initAction() {
        return ctx -> System.out.println(ctx.getTarget().getId());
    }

    @Override
    public void configure(StateMachineStateConfigurer<String, String> states) throws Exception {
        states
                .withStates()
                .initial("REQUESTED")
                .fork("VALIDATING")
                .state("VALIDATED")
                .end("DONE")
                .and().withStates()
                    .parent("VALIDATING")
                    .initial("VALIDATING_LIMIT")
                    .end("LIMIT_VALIDATED")
                .and().withStates()
                    .parent("VALIDATING")
                    .initial("VALIDATING_FRAUD")
                    .end("FRAUD_VALIDATED");
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<String, String> transitions) throws Exception {
        transitions
                .withExternal().source("REQUESTED").target("VALIDATING").event("VALIDATE")
                .and().withFork().source("VALIDATING").target("VALIDATING_LIMIT").target("VALIDATING_FRAUD")
                .and().withJoin().source("LIMIT_VALIDATED").source("FRAUD_VALIDATED").target("VALIDATED")
                .and().withExternal().source("VALIDATED").target("DONE").event("DEBIT");

    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<String, String> config)
            throws Exception {
        config
                .withPersistence()
                .runtimePersister(_stateMachineRuntimePersister);
    }

}
