package com.zzz.service;

import com.zzz.Util.Result;
import com.zzz.pojo.entity.Contact;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zzz
 * @since 2022-03-24
 */
public interface ContactService extends IService<Contact> {

    Result getChatList(Long studentId);

}
