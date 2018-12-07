package com.learn.java.rabbitmq.example.prefetch;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RabbitConsumer {

    private static final String QUEUE_NAME_1 = "queue_demo_prefetch_1";
    private static final String QUEUE_NAME_2 = "queue_demo_prefetch_1";
    private static final String IP_ADDRESS = "192.168.99.102";
    private static final int PORT = 5672;

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        Address[] addresses = new Address[]{new Address(IP_ADDRESS, PORT)};
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("root");
        factory.setPassword("root");
        Connection connection = factory.newConnection(addresses);
        final Channel channel = connection.createChannel();
        Consumer consumer1 = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("consumer1 recv message:" + new String(body));
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        Consumer consumer2 = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("consumer2 recv message:" + new String(body));
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        //global=false；打印出20条日志。两个消费者未ack数都为10
        channel.basicQos(10, false);
        //global=false；打印出10条日志，都为consumer1。两个消费者未ack数之和为10
//        channel.basicQos(10, true);
        channel.basicConsume(QUEUE_NAME_1, false, consumer1);
        channel.basicConsume(QUEUE_NAME_2, false, consumer2);
        //等待回调函数执行完毕之后，关闭资源
        TimeUnit.SECONDS.sleep(60);
        channel.close();
        connection.close();
    }
}
