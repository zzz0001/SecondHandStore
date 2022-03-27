package com.zzz.controller;


import com.zzz.Util.Result;
import com.zzz.service.ContactService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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

    @RequiresAuthentication
    @GetMapping("/contact/{studentId}")
    public Result getContact(@PathVariable Long studentId){
        Result result = contactService.getChatList(studentId);
        return result;
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

