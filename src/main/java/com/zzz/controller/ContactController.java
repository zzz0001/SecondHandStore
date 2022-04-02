package com.zzz.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zzz.Util.JwtUtils;
import com.zzz.Util.Result;
import com.zzz.pojo.entity.Contact;
import com.zzz.pojo.entity.User;
import com.zzz.service.ContactService;
import com.zzz.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zzz
 * @since 2022-03-24
 */
@Slf4j
@RestController
public class ContactController {

    @Resource
    private ContactService contactService;

    @Resource
    private UserService userService;

    @Resource
    private JwtUtils jwtUtils;

    @RequiresAuthentication
    @GetMapping("/contact/{studentId}")
    public Result getContact(@PathVariable Long studentId){
        Result result = contactService.getChatList(studentId);
        return result;
    }

    @RequiresAuthentication
    @PostMapping("/contact/{studentId}")
    public Result addContact(@PathVariable Long studentId,
                             HttpServletRequest request){
        User user = userService.getById(studentId);
        if(user == null){
            return Result.fail("不存在该用户");
        }
        Long myId = jwtUtils.getStudentId(request);
        QueryWrapper<Contact> wrapper = new QueryWrapper<Contact>().eq("student_id", myId).eq("seller_id", studentId);
        Contact one = contactService.getOne(wrapper);
        if(one != null){
            return Result.fail("该用户已经在你的聊天列表中");
        }
        Contact contact = new Contact();
        contact.setStudentId(myId);
        contact.setSellerId(studentId);
        boolean save = contactService.save(contact);
        if (save){
            return Result.success("成功");
        }
        return Result.fail("查找失败");
    }

    @DeleteMapping("/contact/{contactId}")
    public Result removeContact(@PathVariable Long contactId){
        boolean remove = contactService.removeById(contactId);
        if (remove){
            return Result.success("删除成功");
        }
        return Result.fail("删除失败");
    }

}

