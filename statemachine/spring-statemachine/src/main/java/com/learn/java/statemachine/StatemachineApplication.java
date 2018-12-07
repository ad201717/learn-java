package com.learn.java.statemachine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.statemachine.StateMachine;

@SpringBootApplication
public class StatemachineApplication implements CommandLineRunner {

    @Autowired
    private StateMachine<RegStatusEnum, RegEventEnum> stateMachine;

    public static void main(String[] args) {
        SpringApplication.run(StatemachineApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        stateMachine.start();
        stateMachine.sendEvent(RegEventEnum.CONNECT);
        stateMachine.sendEvent(RegEventEnum.REGISTER);
        stateMachine.sendEvent(RegEventEnum.REGISTER_FAILED);
        stateMachine.sendEvent(RegEventEnum.CONNECT);
        stateMachine.sendEvent(RegEventEnum.REGISTER);
        stateMachine.sendEvent(RegEventEnum.REGISTER_SUCCESS);
        stateMachine.sendEvent(RegEventEnum.UN_REGISTER);
        stateMachine.stop();
    }
}
