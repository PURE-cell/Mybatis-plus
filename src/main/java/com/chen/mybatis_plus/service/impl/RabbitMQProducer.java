package com.chen.mybatis_plus.service.impl;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
public class RabbitMQProducer {
    private final static String QUEEN_NAME = "Hello World";
    private final static String MESSAGE = "第一个RabbitMQ";

    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");//设置主机名
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(QUEEN_NAME, false, false, false, null);
            channel.basicPublish("", QUEEN_NAME, null, MESSAGE.getBytes());
            log.info("发送消息：'{}'", MESSAGE);
            channel.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

}
