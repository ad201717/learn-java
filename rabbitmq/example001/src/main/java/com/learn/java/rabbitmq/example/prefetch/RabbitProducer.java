package com.learn.java.rabbitmq.example.prefetch;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitProducer {

    private static final String EXCHANGE_NAME = "exchange_demo_prefetch";
    private static final String QUEUE_NAME_1 = "queue_demo_prefetch_1";
    private static final String QUEUE_NAME_2 = "queue_demo_prefetch_2";
    private static final String IP_ADDRESS = "192.168.99.102";
    private static final int PORT = 5672;

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(IP_ADDRESS);
        factory.setPort(PORT);
        factory.setUsername("root");
        factory.setPassword("root");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout", true, false, null);
        channel.queueDeclare(QUEUE_NAME_1, false, false, false, null);
        channel.queueDeclare(QUEUE_NAME_2, false, false, false, null);
        channel.queueBind(QUEUE_NAME_1, EXCHANGE_NAME, "test");
        channel.queueBind(QUEUE_NAME_2, EXCHANGE_NAME, "test");
        String message = "Hello World!";
        for (int i = 0; i < 20; i++) {
            channel.basicPublish(EXCHANGE_NAME, "test",
                    MessageProperties.PERSISTENT_TEXT_PLAIN,
                    (message + i).getBytes());
        }
        //关闭资源
        channel.close();
        connection.close();
    }
}
