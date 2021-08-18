package com.chen.mybatis_plus.service.RabbitMQWorkQueen;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 *  @Author: chenchao
 *  @Date: 2021/8/18 10:53
 *  @Description: 学习地址：https://www.rabbitmq.com/tutorials/tutorial-two-java.html
 */
public class NewTask {
    //定义一个消息队列的名称
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
            //发布消息
            String []msg = {"one", "two", "three", "four", "five", "six", "seven", "eight", "night", "ten"};
            String message = String.join("-", msg);//从控制台编译
            System.out.println(message);
            for (int i = 0; i < 10; i++) {
                //我们需要将我们的消息标记为持久性 - 通过将MessageProperties（实现BasicProperties）设置为值PERSISTENT_TEXT_PLAIN
                channel.basicPublish("", QUEEN_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));
            }
//            //我们需要将我们的消息标记为持久性 - 通过将MessageProperties（实现BasicProperties）设置为值PERSISTENT_TEXT_PLAIN
//            channel.basicPublish("", QUEEN_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));
            //关闭通道
            channel.close();
            //关闭连接
            connection.close();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
