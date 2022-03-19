package com.zzz.service.impl;

import com.zzz.pojo.entity.Chat;
import com.zzz.mapper.ChatMapper;
import com.zzz.service.ChatService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 聊天表 服务实现类
 * </p>
 *
 * @author zzz
 * @since 2022-03-03
 */
@Service
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Chat> implements ChatService {

}
