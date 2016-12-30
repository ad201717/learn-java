package com.howe.learn.rocketMQ.simple;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;

/**
 * @Author Karl
 * @Date 2016/12/23 15:07
 */
public class Producer {

    public static void main(String[] args) throws MQClientException {

        DefaultMQProducer producer = new DefaultMQProducer("ProducerGroupName-simple");
        producer.setNamesrvAddr("192.168.88.128:9876");
        producer.setInstanceName("Producer-simple");

        producer.start();

        for (int i = 0; i < 5; i++) {
            try {
                {
                    Message msg = new Message("Topic-simple1",
                            "Tag-simple1",
                            "OrderId1-" + i,
                            "Hello RocketMQ".getBytes());
                    SendResult sendResult = producer.send(msg);
                    System.out.println(sendResult);
                }

                {
                    Message msg = new Message("Topic-simple2",
                            "Tag-simple2",
                            "OrderId2-" + i,
                            "Hello RocketMQ".getBytes());
                    SendResult sendResult = producer.send(msg);
                    System.out.println(sendResult);
                }

                {
                    Message msg = new Message("Topic-simple3",
                            "Tag-simple3",
                            "OrderId3-" + i,
                            "Hello RocketMQ".getBytes());
                    SendResult sendResult = producer.send(msg);
                    System.out.println(sendResult);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (RemotingException e) {
                e.printStackTrace();
            } catch (MQBrokerException e) {
                e.printStackTrace();
            }
        }

        producer.shutdown();
    }
}
