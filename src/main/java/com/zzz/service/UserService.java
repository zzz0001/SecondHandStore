package com.zzz.service;

import com.zzz.Util.Result;
import com.zzz.pojo.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author zzz
 * @since 2022-03-02
 */
public interface UserService extends IService<User> {

    Result setPicture(MultipartFile file, HttpServletRequest request);

    Result updateStatus(Long userId);

    Result getUsersAndStore(Integer page);

    Result getByStudentId(Long studentId);
}
