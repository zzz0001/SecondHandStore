package com.zzz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zzz.Util.Result;
import com.zzz.mapper.ContactMapper;
import com.zzz.pojo.entity.Chat;
import com.zzz.mapper.ChatMapper;
import com.zzz.pojo.entity.Contact;
import com.zzz.service.ChatService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

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

    @Resource
    private ContactMapper contactMapper;

    @Transactional
    @Override
    public Result saveChat(Chat chat) {
        chat.setDeleted(false);
        int insert = baseMapper.insert(chat);

        QueryWrapper<Contact> wrapper = new QueryWrapper<Contact>().eq("student_id", chat.getSendId()).eq("seller_id", chat.getReceiveId());
        Contact one = contactMapper.selectOne(wrapper);

        QueryWrapper<Contact> wrapper2 = new QueryWrapper<Contact>().eq("student_id", chat.getReceiveId()).eq("seller_id", chat.getSendId());
        Contact one2 = contactMapper.selectOne(wrapper2);

        Contact contact = new Contact();
        contact.setStudentId(chat.getSendId());
        contact.setSellerId(chat.getReceiveId());

        Contact contact2 = new Contact();
        contact2.setStudentId(chat.getReceiveId());
        contact2.setSellerId(chat.getSendId());

        if (one == null){
            contactMapper.insert(contact);
        }
        if (one2 == null){
            contactMapper.insert(contact2);
        }
        if (insert == 1) {
            return Result.success("消息发送成功");
        }
        return Result.fail("消息发送失败");
    }

    @Override
    public Result ReadChat(Long chatId) {
        Chat chat = baseMapper.selectById(chatId);
        chat.setIsRead(0);
        int update = baseMapper.updateById(chat);
        if (update == 1){
            return Result.success("消息已读");
        }
        return Result.fail("消息未读");
    }

    @Override
    public Integer newChatNum(Long sendId, Long receiveId) {
        QueryWrapper<Chat> wrapper = new QueryWrapper<Chat>().eq("send_id", sendId).eq("receive_id", receiveId).eq("is_read",1);
        Integer count = baseMapper.selectCount(wrapper);
        return count;
    }

    @Override
    public Result ReadChatList(List<Long> chatList) {
        chatList.forEach(chatId ->{
            Chat chat = baseMapper.selectById(chatId);
            chat.setIsRead(0);
            baseMapper.updateById(chat);
        });
        return Result.success("消息已读");
    }
}
