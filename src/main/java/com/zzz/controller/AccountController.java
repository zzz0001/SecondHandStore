package com.zzz.controller;


import cn.hutool.crypto.SecureUtil;
import com.zzz.Util.JwtUtils;
import com.zzz.Util.Result;
import com.zzz.pojo.entity.Account;
import com.zzz.pojo.entity.vo.AccountVo;
import com.zzz.pojo.entity.vo.PasswordVo;
import com.zzz.service.AccountService;
import com.zzz.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 账户 前端控制器
 * </p>
 *
 * @author zzz
 * @since 2022-02-28
 */
@Slf4j
@RestController
public class AccountController {

    @Resource
    private AccountService accountService;

    @Resource
    private UserService userService;

    @Resource
    private JwtUtils jwtUtils;

    @RequiresAuthentication
    @PostMapping("/account")
    public Result account(@RequestBody Account account){
        Result result = accountService.saveAccount(account);
        return result;
    }

    @RequiresAuthentication
    @GetMapping("/account/{studentId}")
    public Result getAccount(@PathVariable Long studentId){
        Account account = accountService.getById(studentId);
        if(account == null){
            return Result.success(null);
        }
        return Result.success(account);
    }

    @RequiresAuthentication
    @PutMapping("/account/addMoney")
    public Result addMoney(@RequestBody AccountVo accountVo,
                           HttpServletRequest request){
        Double money = accountVo.getMoney();
        if (money<=0||money>=100000){
            return Result.fail("充值失败，一次只能充值(1-100000)元");
        }
        Long studentId = jwtUtils.getStudentId(request);
        // 根据学号充值余额
        Result result = accountService.addMoney(studentId,money,accountVo.getPassword());
        return result;
    }

    @RequiresAuthentication
    @PutMapping("/account/reduceMoney")
    public Result reduceMoney(@RequestBody AccountVo accountVo,
                           HttpServletRequest request){
        Double money = accountVo.getMoney();
        if (money<=0||money>=100000){
            return Result.fail("提现失败，一次只能提现(1-100000)元");
        }
        Long studentId = jwtUtils.getStudentId(request);
        // 根据学号提现余额
        Result result = accountService.reduceMoney(studentId,money,accountVo.getPassword());
        return result;
    }

    @RequiresRoles("root")
    @RequiresAuthentication
    @PutMapping("/account/{studentId}")
    public Result changeStatus(@PathVariable Long studentId){
        Account account = accountService.getById(studentId);
        Integer status = account.getStatus();
        if (status == 1){
            account.setStatus(0);
        }else{
            account.setStatus(1);
        }
        boolean update = accountService.updateById(account);
        if (update) {
            return Result.success("账户状态修改成功");
        }
        return Result.fail("账户状态修改失败");
    }

    @RequiresAuthentication
    @PutMapping("/account/password")
    public Result ChangePassword(@RequestBody PasswordVo passwordVo){
        Long studentId = passwordVo.getStudentId();
        Account account = accountService.getById(studentId);
        if (!account.getPassword().equals(SecureUtil.md5(passwordVo.getOldPassword()))){
            return Result.fail("原密码错误");
        }
        account.setPassword(SecureUtil.md5(passwordVo.getPassword()));
        boolean update = accountService.updateById(account);
        if(update){
            return Result.success("支付密码修改成功");
        }
        return Result.fail("支付密码修改失败");
    }

    @RequiresAuthentication
    @DeleteMapping("/account/{studentId}")
    public Result remove(@PathVariable Long studentId){
        boolean lock = accountService.isLock(studentId);
        if (lock){
            return Result.fail("账户被锁定，不允许注销账户");
        }
        boolean b = accountService.removeById(studentId);
        if (b){
            return Result.success("账户注销成功");
        }
        return Result.fail("账户注销失败");
    }



}

