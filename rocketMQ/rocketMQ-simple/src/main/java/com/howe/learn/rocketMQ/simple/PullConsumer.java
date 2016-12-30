package com.howe.learn.rocketMQ.simple;

import com.alibaba.rocketmq.client.consumer.*;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.common.message.MessageQueue;
import com.alibaba.rocketmq.remoting.exception.RemotingException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @Author Karl
 * @Date 2016/12/23 15:16
 */
public class PullConsumer {

    public static void main(String[] args) throws MQClientException {

        DefaultMQPullConsumer pullConsumer = new DefaultMQPullConsumer("ConsumeGroupName-simple");
        pullConsumer.setNamesrvAddr("192.168.88.128:9876");
        pullConsumer.setInstanceName("PullConsumer-simple");

        Map<MessageQueue, Long> queueOffsetMap = new HashMap<MessageQueue, Long>();

        pullConsumer.start();

        System.out.println("pushConsumer started");

        Set<MessageQueue> messageQueueSet = pullConsumer.fetchSubscribeMessageQueues("Topic-simple3");

        while(true) {
            for (MessageQueue queue : messageQueueSet) {
                Long offset = queueOffsetMap.get(queue);
                if (null == offset) {
                    offset = 0L;
                }
                try {
                    PullResult pullResult = pullConsumer.pullBlockIfNotFound(queue, "Tag-simple3", offset, 3);
                    if (pullResult.getPullStatus() == PullStatus.FOUND) {
                        queueOffsetMap.put(queue, pullResult.getNextBeginOffset());
                        pullResult.getMsgFoundList()
                                .stream()
                                .map((messageExt)->messageExt.getTopic() + "=>" + new String(messageExt.getBody()))
                                .forEach(System.out::println);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("error");
                }
            }
        }

    }
}
