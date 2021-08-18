package com.chen.mybatis_plus.service.RabbitMQWorkQueen;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
public class WorkConsumer {
    //定义消息队列名称
    private static final String QUEEN_NAME = "work";

    public static void main(String[] args) {
        //开启连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //设置主机名称或IP
        factory.setHost("localhost");
        try {
            //开启连接，抛出异常
            Connection connection = factory.newConnection();
            //开启连接通道
            Channel channel = connection.createChannel();
            //设置队列参数
            boolean autoAck = true; //消息确认与持久性，消费者发回确认消息，告诉 RabbitMQ 特定消息已被接收、处理，并且 RabbitMQ 可以自由删除它。
            channel.queueDeclare(QUEEN_NAME, autoAck, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            channel.basicQos(1);    //公平调度，一次只接受一条未确认的消息

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println("接收到的信息：" + message);
                try {
                    //do somethings
                    doWork(message);
                } finally {
                    System.out.println(" [x] Done");
                    //当您的客户端退出时，消息将被重新传送（这可能看起来像随机重新传送），但 RabbitMQ 将消耗越来越多的内存，因为它无法释放任何未确认的消息。所以利用basicAck()必须在接收交付的同一通道上发送确认
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);//确认消息，设置为false
                }
            };
            channel.basicConsume(QUEEN_NAME, false, deliverCallback, consumerTag -> {});
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    //模拟执行时间的假任务
    private static void doWork(String message) {
        int count = 0;
        for (char ch
        : message.toCharArray()){
            //遇到'-'符号耗时1s
            if (ch == '-'){
                try {
                    log.info("等待时间：{}秒" , ++count);
                    Thread.sleep(1000);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
