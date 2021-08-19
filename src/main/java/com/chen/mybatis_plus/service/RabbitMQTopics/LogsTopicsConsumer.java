package com.chen.mybatis_plus.service.RabbitMQTopics;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class LogsTopicsConsumer {
    private static final String EXCHANGE_NAME = "topic_logs";

    public static void consumer(String []routingKey){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = null;
        try {
            connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare(EXCHANGE_NAME, "topic");

            String queueName = channel.queueDeclare().getQueue();

            for (String key :
                    routingKey) {
                channel.queueBind(queueName, EXCHANGE_NAME, key);
            }

            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received '" +
                        delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        String[] routingKey1 = {"*.orange.*"};
        String[] routingKey2 = {"*.*.rabbit"};
        String[] routingKey3 = {"lazy.#"};
        consumer(routingKey1);
        consumer(routingKey2);
        consumer(routingKey3);
    }

}
