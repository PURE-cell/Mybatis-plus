package com.chen.mybatis_plus.service.RabbitMQFanout;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

/**
 *  @Author: chenchao
 *  @Date: 2021/8/18 15:02
 *  @Description: RabbitMQ订阅模型：Fanout
 *  学习地址：https://www.rabbitmq.com/tutorials/tutorial-three-java.html
 */
public class EmitLog {
    //创建交换机名称
    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) {
        //创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //设置主机名或者IP
        factory.setHost("localhost");
        try {
            //创建连接
            Connection connection = factory.newConnection();
            //开启连接通道
            Channel channel = connection.createChannel();
            //定义交换机类型为Fanout
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
            //设置发布的消息
            String []msg = {"one", "two", "three", "four", "five", "six", "seven", "eight", "night", "ten"};
            for (int i = 0; i < 10; i++) {
                channel.basicPublish(EXCHANGE_NAME, "", null, msg[i].getBytes("UTF-8"));
            }
            System.out.println(Arrays.toString(msg));
            channel.close();
            connection.close();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }

    }
}
