

/*
 *
 *  Copyright (C) 2016 Fangcang Limited
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.howe.learn.rocketMQ.delayed;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;

/**
 * @Author Karl
 * @Date 2016/12/31 15:49
 */
public class DelayedProducer {

    public static void main(String[] args) {
        DefaultMQProducer producer = new DefaultMQProducer("delayedGroup");
        producer.setNamesrvAddr("192.168.88.128:9876");
        producer.setInstanceName("delayedMessageProducer");
        try {
            producer.start();

            producer.createTopic("delayed", "delayed", 2);

            Message message = new Message("delayed", "hello".getBytes());
            message.setDelayTimeLevel(3);

            SendResult result = producer.send(message);
            System.out.println(result);
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            producer.shutdown();
        }
    }
}
