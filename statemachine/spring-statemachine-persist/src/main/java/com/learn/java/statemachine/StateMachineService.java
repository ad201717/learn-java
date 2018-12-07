package com.learn.java.statemachine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class StateMachineService {

    @Autowired
    private StateMachinePersister<RegStatusEnum, RegEventEnum, String> stateMachinePersister;

    @Autowired
    private StateMachineFactory<RegStatusEnum, RegEventEnum> stateMachineFactory;

    public void execute(String businessId, RegEventEnum event, Map<String, Object> context) {
        // 利用随记ID创建状态机，创建时没有与具体定义状态机绑定
        StateMachine<RegStatusEnum, RegEventEnum> stateMachine = stateMachineFactory.getStateMachine(UUID.randomUUID());
        stateMachine.start();
        try {
            stateMachinePersister.restore(stateMachine, businessId);

            MessageBuilder<RegEventEnum> messageBuilder = MessageBuilder.withPayload(event)
                    .setHeader("BusinessId", businessId);
            if (null != context) {
                context.entrySet().forEach(entry -> messageBuilder.setHeader(entry.getKey(), entry.getValue()));
            }

            boolean success = stateMachine.sendEvent(messageBuilder.build());
            if (success) {
                stateMachinePersister.persist(stateMachine, businessId);
            } else {
                System.err.println("状态机处理未执行成功，请处理，ID：" + businessId + "，当前context：" + context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            stateMachine.stop();
        }
    }
}