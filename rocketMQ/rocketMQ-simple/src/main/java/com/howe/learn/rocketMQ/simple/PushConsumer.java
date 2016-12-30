package com.howe.learn.rocketMQ.simple;

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

        DefaultMQPushConsumer pushConsumer = new DefaultMQPushConsumer("ConsumeGroupName-simple");
        pushConsumer.setNamesrvAddr("192.168.88.128:9876");
        pushConsumer.setInstanceName("PushConsumer-simple");

        pushConsumer.subscribe("Topic-simple1", "Tag-simple1 || Tag-simple2");
        pushConsumer.subscribe("Topic-simple2", "*");

        pushConsumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                System.out.println(Thread.currentThread().getName() + " Receive New Messages:" + list.size());

                for(MessageExt msg : list){
                    if("Topic-simple1".equals(msg.getTopic())){
                        System.out.println("Message of Topic-simple1, tag:" + msg.getTags() + ", body:" + new String(msg.getBody()));
                    } else if("Topic-simple1".equals(msg.getTopic())){
                        System.out.println("Message of Topic-simple2, tag:" + msg.getTags() + ", body:" + new String(msg.getBody()));
                    } else {
                        System.out.println("Unknown Msg:" + msg);
                    }
                }

                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        pushConsumer.start();

        System.out.println("pushConsumer started");
    }
}
