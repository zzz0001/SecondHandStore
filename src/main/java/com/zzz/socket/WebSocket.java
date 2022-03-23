package com.zzz.socket;

import cn.hutool.json.JSONUtil;
import com.alibaba.druid.support.json.JSONUtils;
import com.zzz.Util.MessageUtils;
import com.zzz.Util.SpringBeansUtils;
import com.zzz.pojo.entity.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zzz
 * @date 2022/3/22 22:27
 */

@ServerEndpoint("/webSocket/{studentId}")
@Slf4j
@Component
public class WebSocket {

    private static int onlineCount = 0;
    private static Map<String, WebSocket> clients = new ConcurrentHashMap<>();
    private Session session;
    private String studentId;

    @OnOpen
    public void onOpen(@PathParam("studentId") String studentId, Session session) throws IOException {
        this.studentId = studentId;
        this.session = session;
        clients.put(studentId, this);
        WebSocket.onlineCount++;

        RedisTemplate redisTemplate = (RedisTemplate) SpringBeansUtils.getBean("redisTemplate");

        if (redisTemplate.hasKey(studentId)){
            List messageList = redisTemplate.boundListOps(studentId).range(0, -1);
            String message = JSONUtils.toJSONString(messageList);
            this.session.getBasicRemote().sendText(message);
            redisTemplate.delete(studentId);
        }
    }

    @OnClose
    public void onClose(Session session,CloseReason closeReason) {
        clients.remove(studentId);
        WebSocket.onlineCount--;
    }

    // 接收到客户端的消息以后
    @OnMessage
    public void onMessage(@PathParam("studentId") String studentId,String message) {
        Message message1 = JSONUtil.toBean(message, Message.class);
        String toName = message1.getToName();
        String resultMessage = MessageUtils.getMessage(false, studentId, message1.getMessage());
        try {
            WebSocket webSocket = clients.get(toName);
            if(webSocket != null) {
                webSocket.session.getBasicRemote().sendText(resultMessage);
            }else{
                RedisTemplate redisTemplate = (RedisTemplate) SpringBeansUtils.getBean("redisTemplate");
                redisTemplate.boundListOps(toName+"message").rightPush(studentId);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("WebSocket发生错误：" + throwable);
    }

    public void sendMessage(String studentId,String message) {
        // 可以修改为对某个客户端发消息
        try {
            WebSocket socket = clients.get(studentId);
            if (socket != null){
                socket.session.getBasicRemote().sendText(message);
            }else {
                RedisTemplate redisTemplate = (RedisTemplate) SpringBeansUtils.getBean("redisTemplate");
                redisTemplate.boundListOps(studentId).rightPush(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageToAll(String message) {
//         向所有连接websocket的客户端发送消息
        for (WebSocket item : clients.values()) {
            item.session.getAsyncRemote().sendText(message);
        }
    }

}

