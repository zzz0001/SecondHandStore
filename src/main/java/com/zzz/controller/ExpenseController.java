package com.zzz.controller;


import com.zzz.Util.Result;
import com.zzz.pojo.entity.User;
import com.zzz.service.ExpenseService;
import com.zzz.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 花销表 前端控制器
 * </p>
 *
 * @author zzz
 * @since 2022-03-29
 */
@Slf4j
@RestController
public class ExpenseController {

    @Resource
    private ExpenseService expenseService;

    @Resource
    private UserService userService;

    @RequiresAuthentication
    @GetMapping("/expense/{studentId}")
    public Result getExpense(@PathVariable Long studentId){
        Result result = expenseService.getExpense(studentId);
        return result;
    }

    @RequiresAuthentication
    @GetMapping("/expense/page/{studentId}/{page}")
    public Result getExpensePage(@PathVariable Long studentId,
                                 @PathVariable Integer page){
        User user = userService.getById(studentId);
        if (user == null){
            return Result.fail("用户不存在");
        }
        Result result = expenseService.getExpensePage(studentId,page);
        return result;
    }


}

