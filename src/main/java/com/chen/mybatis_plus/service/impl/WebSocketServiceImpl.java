package com.chen.mybatis_plus.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@ServerEndpoint("/websocket")
@Slf4j
public class WebSocketServiceImpl {
    private Session session;
    private static CopyOnWriteArrayList<WebSocketServiceImpl> webSockets = new CopyOnWriteArrayList<>();

    @OnOpen
    public void onOpen(Session session){
        this.session = session;
        webSockets.add(this);
        log.info("websocket有新的连接，总连接数:{}",webSockets.size());
    }

    @OnClose
    public void onClose(){
        webSockets.remove(this);
        log.info("websocket连接已断开，总连接数为：{}",webSockets.size());
    }

    @OnMessage
    public void onMessage(String message){
        log.info("从客户端收到的消息:{}", message);
    }

    public void sendMessage(String message){
        for (WebSocketServiceImpl webSocket : webSockets){
            log.info("websocket广播消息,message={}", message);
            try {
                webSocket.session.getBasicRemote().sendText(message);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
