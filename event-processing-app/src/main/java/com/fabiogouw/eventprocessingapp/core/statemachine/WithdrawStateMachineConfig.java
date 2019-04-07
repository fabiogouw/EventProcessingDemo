package com.fabiogouw.eventprocessingapp.core.statemachine;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@Configuration
@EnableStateMachine
public class WithdrawStateMachineConfig extends StateMachineConfigurerAdapter<String, String> {

    public Action<String, String> initAction() {
        return ctx -> System.out.println(ctx.getTarget().getId());
    }

    @Override
    public void configure(StateMachineStateConfigurer<String, String> states) throws Exception {
        states
                .withStates()
                .initial("REQUESTED")
                .fork("VALIDATING")
                .join("REQUIREMENTS_VALIDATED")
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
                .and().withExternal().source("VALIDATING_LIMIT").target("LIMIT_VALIDATED").event("LIMIT_OK")
                .and().withExternal().source("VALIDATING_FRAUD").target("FRAUD_VALIDATED").event("FRAUD_OK")
                .and().withJoin().source("VALIDATING").target("REQUIREMENTS_VALIDATED")
                .and().withExternal().source("REQUIREMENTS_VALIDATED").target("VALIDATED")
                .and().withExternal().source("VALIDATED").target("DONE").event("DEBIT");

    }
}
