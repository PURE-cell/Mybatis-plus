package com.chen.mybatis_plus.service.RabbitMQDirect;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 *  @Author: chenchao
 *  @Date: 2021/8/18 16:14
 *  @Description: 订阅模式：Direct
 */
public class EmitLogDirect {
    //创建交换机名称
    private static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) {
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
            //设置绑定key与信息
            String []bindKey = {"info","warn", "error", "test"};
            String []message = {"Hello","Warning","Error","ok"};
            //发布消息
            for (int i = 0; i < bindKey.length; i++) {
                channel.basicPublish(EXCHANGE_NAME, bindKey[i], null, message[i].getBytes("UTF-8"));
                System.out.println(" [x] Sent '" + bindKey[i] + "':'" + message[i] + "'");
            }
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }

    }
}
