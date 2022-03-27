package com.zzz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzz.Util.Result;
import com.zzz.mapper.ContactMapper;
import com.zzz.mapper.StoreMapper;
import com.zzz.mapper.UserMapper;
import com.zzz.pojo.entity.Contact;
import com.zzz.pojo.entity.Store;
import com.zzz.pojo.entity.User;
import com.zzz.service.ChatService;
import com.zzz.service.ContactService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zzz
 * @since 2022-03-24
 */
@Service
public class ContactServiceImpl extends ServiceImpl<ContactMapper, Contact> implements ContactService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private StoreMapper storeMapper;

    @Resource
    private ChatService chatService;

    @Override
    public Result getChatList(Long studentId) {
        QueryWrapper<Contact> wrapper = new QueryWrapper<Contact>().eq("student_id", studentId).orderByDesc("contact_id");
        List<Contact> contacts = baseMapper.selectList(wrapper);
        ArrayList<Object> result = new ArrayList<>();
        contacts.forEach(contact -> {
            HashMap<String, Object> map = new HashMap<>();
            Long sellerId = contact.getSellerId();
            Integer newChatNum = chatService.newChatNum(sellerId, studentId);
            User user = userMapper.selectById(sellerId);
            Store store = storeMapper.selectOne(new QueryWrapper<Store>().eq("student_id", sellerId));
            map.put("contact",contact);
            map.put("user",user);
            map.put("store",store);
            map.put("newChatNum",newChatNum);
            result.add(map);
        });
        return Result.success(result);
    }
}
