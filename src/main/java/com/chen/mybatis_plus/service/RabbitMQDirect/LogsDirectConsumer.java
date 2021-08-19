package com.chen.mybatis_plus.service.RabbitMQDirect;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class LogsDirectConsumer {
    //创建交换机名称
    private static final String EXCHANGE_NAME = "direct_logs";

    //创建线程订阅'info','warn','error','test'
    public static Runnable runnable = () -> {
        //创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //设置主机名或IP
        factory.setHost("localhost");
        try {
            //创建连接
            Connection connection = factory.newConnection();
            //开启连接通道
            Channel channel = connection.createChannel();
            //定义交换机类型与名称
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");
            //创建临时队列
            String queueName = channel.queueDeclare().getQueue();
            //绑定队列、交换机和路由密钥
            String []bindKey = {"info","warn", "error", "test"};
            for (String severity :
                    bindKey) {
                channel.queueBind(queueName, EXCHANGE_NAME, severity);
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
    };

    //创建线程只订阅'info','warn','error'
    public static Runnable runnable1 = () -> {
        //创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //设置主机名或IP
        factory.setHost("localhost");
        try {
            //创建连接
            Connection connection = factory.newConnection();
            //开启连接通道
            Channel channel = connection.createChannel();
            //定义交换机类型与名称
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");
            //创建临时队列
            String queueName = channel.queueDeclare().getQueue();
            //绑定队列、交换机和路由密钥
            String []bindKey = {"info","warn", "error"};
            for (String severity :
                    bindKey) {
                channel.queueBind(queueName, EXCHANGE_NAME, severity);
            }
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received queue2 '" +
                        delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    };

    public static void main(String[] args) {
        new Thread(runnable, "queue1").start();//订阅'info','warn','error','test'
        new Thread(runnable1, "queue2").start();//只订阅'info','warn','error'
        new Thread(runnable, "queue3").start();//订阅'info','warn','error','test'
    }
}
