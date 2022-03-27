package com.zzz.service;

import com.zzz.Util.Result;
import com.zzz.pojo.entity.Chat;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 聊天表 服务类
 * </p>
 *
 * @author zzz
 * @since 2022-03-03
 */
public interface ChatService extends IService<Chat> {

    Result saveChat(Chat chat);

    Result ReadChat(Long chatId);

    Integer newChatNum(Long sendId,Long receiveId);

    Result ReadChatList(List<Long> chatList);
}
