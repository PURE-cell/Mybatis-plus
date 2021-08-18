package com.chen.mybatis_plus.service.impl;

import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
public class RabbitMQConsumer {
    private final static String QUEEN_NAME = "Hello World";

    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(QUEEN_NAME, false, false, false, null);
            /**
             *  @Author: chenchao
             *  @Date: 2021/8/17 17:28
             *  @Description: 第一种方式利用匿名类创建
             */
//            Consumer consumer = new DefaultConsumer(channel){
//                @Override
//                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
//                    String message = new String(body, "UTF-8");
//                    log.info("消费者收到消息：'{}'", message);
//                }
//            };
//            channel.basicConsume(QUEEN_NAME, true, consumer);
            /**
             *  @Author: chenchao
             *  @Date: 2021/8/17 17:28
             *  @Description: 第二种方式利用Lambda创建
             */
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                log.info("消费者收到消息：'{}'", message);
            };
            channel.basicConsume(QUEEN_NAME, true, deliverCallback, consumerTag -> {});
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

    }
}
