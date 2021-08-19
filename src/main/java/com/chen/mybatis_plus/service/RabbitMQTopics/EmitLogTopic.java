package com.chen.mybatis_plus.service.RabbitMQTopics;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Author: chenchao
 * @Date: 2021/8/19 9:42
 * @Description: RabbitMQ订阅模型： Topics
 * 学习网址： https://www.rabbitmq.com/tutorials/tutorial-five-java.html
 */
public class EmitLogTopic {
    private static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "topic");

        String[] routingKey = {"quick.orange.rabbit", "lazy.orange.elephant", "quick.orange.fox", "lazy.brown.fox",
                "lazy.pink.rabbit", "quick.brown.fox", "quick.orange.male.rabbit", "lazy.orange.male.rabbit"};

        String[] message = {"快.橙子.兔子", "慢.橙色.大象", "快.橙色.狐狸", "慢.棕色.狐狸",
                "慢.粉色.兔子", "快.棕色.狐狸", "快.橙色.雄性.兔子", "慢.橙色.雄性.兔子"};

        for (int i = 0; i < routingKey.length; i++) {
            channel.basicPublish(EXCHANGE_NAME, routingKey[i], null, message[i].getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + routingKey[i] + "':'" + message[i] + "'");
        }
        channel.close();
        connection.close();
    }

}
