/*
 * *
 *  * Copyright (C) 2016 Fangcang Limited
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.howe.learn.rocketMQ.delayed;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * @Author Karl
 * @Date 2016/12/23 15:16
 */
public class PushConsumer {

    public static void main(String[] args) throws MQClientException {

        DefaultMQPushConsumer pushConsumer = new DefaultMQPushConsumer("delayedGroup");
        pushConsumer.setNamesrvAddr("192.168.88.128:9876");
        pushConsumer.setInstanceName("delayedMessageConsumer");

        pushConsumer.subscribe("delayed", "*");

        pushConsumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                System.out.println(Thread.currentThread().getName() + " Receive New Messages:" + list.size());

                for(MessageExt msg : list){
                    System.out.println("Message of delayed, body:" + new String(msg.getBody()));
                }

                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        pushConsumer.start();

        System.out.println("pushConsumer started");
    }
}
