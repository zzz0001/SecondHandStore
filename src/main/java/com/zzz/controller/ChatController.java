package com.zzz.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zzz.Util.JwtUtils;
import com.zzz.Util.Result;
import com.zzz.pojo.entity.Chat;
import com.zzz.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 聊天表 前端控制器
 * </p>
 *
 * @author zzz
 * @since 2022-03-03
 */
@Slf4j
@RestController
public class ChatController {

    @Resource
    private ChatService chatService;

    @Resource
    private JwtUtils jwtUtils;

    @RequiresAuthentication
    @GetMapping("/chat/{receiveId}")
    public Result chat(@PathVariable Long receiveId,
                       HttpServletRequest request) {
        Long studentId = jwtUtils.getStudentId(request);
        QueryWrapper<Chat> wrapper = new QueryWrapper<Chat>();
        wrapper.and(Wrapper -> Wrapper.eq("send_id", studentId).eq("receive_id", receiveId))
                .or(Wrapper -> Wrapper.eq("send_id", receiveId).eq("receive_id", studentId))
                .orderByDesc("create_time");
        List<Chat> chats = chatService.list(wrapper);
        return Result.success(chats);
    }

    @RequiresAuthentication
    @PostMapping("/chat")
    public Result chat(@RequestBody Chat chat) {
        chat.setDeleted(false);
        boolean save = chatService.save(chat);
        if (save) {
            return Result.success("消息发送成功");
        }
        return Result.fail("消息发送失败");
    }

    @RequiresAuthentication
    @DeleteMapping("/chat/{chatId}")
    public Result remove(@PathVariable Long chatId) {
        boolean remove = chatService.removeById(chatId);
        if (remove) {
            return Result.success("消息删除成功");
        }
        return Result.fail("消息删除失败");
    }


}

