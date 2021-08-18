package com.chen.mybatis_plus.service.RabbitMQFanout;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class LogsConsumer {
    //创建交换机名称
    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) {
        //创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //设置主机名或IP
        factory.setHost("localhost");
        try {
            //创建连接
            Connection connection = factory.newConnection();
            //开启通道
            Channel channel = connection.createChannel();
            //定义交换机名称
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
            //创建随机队列
            String queueName = channel.queueDeclare().getQueue();
            //绑定随机队列与交换机
            channel.queueBind(queueName, EXCHANGE_NAME, "");

            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message =  new String(delivery.getBody(),"UTF-8");
                System.out.println(" [x] Received '" + message + "'");
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});

        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
