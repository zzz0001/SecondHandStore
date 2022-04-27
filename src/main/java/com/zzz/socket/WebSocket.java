package com.zzz.socket;

import cn.hutool.json.JSONUtil;
import com.zzz.Util.MessageUtils;
import com.zzz.pojo.entity.Chat;
import com.zzz.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author zzz
 * @date 2022/3/22 22:27
 */

@ServerEndpoint("/webSocket/{type}/{studentId}")
@Slf4j
@Component
public class WebSocket {

    private static Map<String, WebSocket> clients = new ConcurrentHashMap<>();
    private Session session;
    private String studentId;

    private static RedisTemplate redisTemplate;
    private static ChatService chatService;

    @Autowired
    public void setApplicationContext(RedisTemplate redisTemplate, ChatService chatService){
        WebSocket.redisTemplate = redisTemplate;
        WebSocket.chatService = chatService;
    }

    @OnOpen
    public void onOpen(@PathParam("studentId") String studentId, @PathParam("type") String type,Session session) throws IOException {
        this.studentId = studentId;
        this.session = session;
        clients.put(studentId, this);
        // 订单消息,返回新订单消息数
        if ("store".equals(type)){
            if (redisTemplate.hasKey(studentId)){
                Integer newOrderNumber = (Integer) redisTemplate.opsForValue().get(studentId);
                this.session.getBasicRemote().sendText(newOrderNumber.toString());
                redisTemplate.delete(studentId);
            }
        }
        if ("chatList".equals(type)){
            // 聊天消息
            if (redisTemplate.hasKey(studentId)){
                Integer newChatNum = (Integer) redisTemplate.opsForValue().get(studentId);
                this.session.getBasicRemote().sendText(newChatNum.toString());
                redisTemplate.delete(studentId);
            }
        }

    }

    @OnClose
    public void onClose(Session session,CloseReason closeReason) {
        clients.remove(studentId);
    }

    // 接收到客户端的消息以后
    @OnMessage
    public void onMessage(@PathParam("studentId") String studentId,String message) {
        Chat chat = JSONUtil.toBean(message, Chat.class);
        chat.setDeleted(false);
        String toName = chat.getReceiveId().toString();
        String resultMessage = MessageUtils.getMessage(studentId, chat.getMessage());
        try {
            WebSocket webSocket = clients.get("chat"+toName);
            if(webSocket != null) {
                webSocket.session.getBasicRemote().sendText(resultMessage);
                chat.setIsRead(0);
            }else{
                webSocket = clients.get("chatList"+toName);
                if(webSocket != null){
                    webSocket.session.getBasicRemote().sendText(Integer.valueOf(1).toString());
                }else {
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    //使用Callable接口作为构造参数
                    FutureTask<Boolean> future = new FutureTask<>(() -> {
                        //真正的任务在这里执行，这里的返回值类型为String，可以为任意类型
                        redisTemplate.opsForValue().increment("chatList"+toName);
                        return null;
                    });
                    try {
                        executor.execute(future);
                        //取得结果，同时设置超时执行时间为1秒。同样可以用future.get()，不设置执行超时时间取得结果
                        future.get(1000, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error("redis连接失败");
                        future.cancel(true);
                    } finally {
                        executor.shutdown();
                    }
                }
                chat.setIsRead(1);
            }
            chatService.saveChat(chat);
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
            WebSocket socket = clients.get("order"+studentId);
            if (socket != null){
                socket.session.getBasicRemote().sendText(message);
            }else {
                ExecutorService executor = Executors.newSingleThreadExecutor();
                //使用Callable接口作为构造参数
                FutureTask<Boolean> future = new FutureTask<>(() -> {
                    //真正的任务在这里执行，这里的返回值类型为String，可以为任意类型
                    redisTemplate.opsForValue().increment("order"+studentId);
                    return null;
                });
                try {
                    executor.execute(future);
                    //取得结果，同时设置超时执行时间为1秒。同样可以用future.get()，不设置执行超时时间取得结果
                    future.get(1000, TimeUnit.MILLISECONDS);
                } catch (Exception e) {
                    future.cancel(true);
                    throw e;
                } finally {
                    executor.shutdown();
                }
            }
        } catch (Exception e) {
            throw new RedisConnectionFailureException("redis连接失败");
        }
    }

    public void sendMessageToAll(String message) {
//         向所有连接websocket的客户端发送消息
        for (WebSocket item : clients.values()) {
            item.session.getAsyncRemote().sendText(message);
        }
    }

}

