package com.learn.java.statemachine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Component;

@Component
public class StateMachinePersist implements org.springframework.statemachine.StateMachinePersist<RegStatusEnum, RegEventEnum, String> {

    @Autowired
    private StateDbService stateDbService;

    @Override
    public void write(StateMachineContext<RegStatusEnum, RegEventEnum> stateMachineContext, String s) throws Exception {
        stateDbService.save(s, stateMachineContext.getState());
    }

    @Override
    public StateMachineContext<RegStatusEnum, RegEventEnum> read(String s) throws Exception {
        RegStatusEnum statusEnum = stateDbService.load(s);
        return null != statusEnum ? new DefaultStateMachineContext<>(statusEnum, null, null, null, null, "stateMachine")
                : new DefaultStateMachineContext<>(RegStatusEnum.UNCONNECTED, null, null, null, null, "stateMachine");
    }
}
