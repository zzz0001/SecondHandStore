package com.zzz.controller;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzz.Util.JwtUtils;
import com.zzz.Util.Result;
import com.zzz.exception.ExceptionEnum;
import com.zzz.pojo.entity.User;
import com.zzz.pojo.entity.vo.UserVo;
import com.zzz.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author zzz
 * @since 2022-02-28
 */
@Slf4j
@RestController
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private JwtUtils jwtUtils;

    @GetMapping("/user/list")
    public Result List() {
        List<User> users = userService.list();
        users.forEach( user -> {
            user.setAge(DateUtil.ageOfNow(user.getBirthDay()));
        });
        return Result.success(users);
    }

    @GetMapping("/user/page/{page}")
    public Result UserPage(@PathVariable Integer page) {
        Page<User> userPage = new Page<>(page, 10);
        Page<User> users = userService.page(userPage, new QueryWrapper<User>().orderByDesc("student_id"));
        List<User> userList = users.getRecords();
        userList.forEach(user -> {
            user.setAge(DateUtil.ageOfNow(user.getBirthDay()));
        });
        return Result.success(users);
    }

    @RequiresAuthentication
    @GetMapping("/user/{studentId}")
    public Result getUser(@PathVariable Long studentId) {
        User user = userService.getById(studentId);
        if (user == null) {
            return Result.fail("不存在该用户");
        }
        user.setAge(DateUtil.ageOfNow(user.getBirthDay()));
        return Result.success(user);
    }

    @PostMapping("/user/register")
    public Result register(@ApiParam("用户信息") @RequestBody User user) {
        Long studentId = user.getStudentId();
        log.info("正在注册新用户---------{}",user);
        User user1 = userService.getById(studentId);
        if (user1 != null) {
            log.info("学号已经注册过了---------{}",user.getStudentId());
            return Result.fail(ExceptionEnum.NAME_EXIST);
        }
        user.setPassword(SecureUtil.md5(user.getPassword()));
        user.setRoles("user");
        user.setPerms("V1");
        user.setStatus(0);
        user.setDeleted(false);
        user.setPicture("/user/123abc.jpeg");
        boolean result = userService.save(user);
        if (result) {
            log.info("用户注册成功---------{}",user);
            return Result.success("注册成功");
        }
        return Result.fail(ExceptionEnum.REGISTER_ERROR);
    }

    @PostMapping("/user/login")
    public Result login(@ApiParam("登录") @RequestBody UserVo userVo,
                        HttpServletResponse response) {
        log.info("用户进行登录---------{}",userVo);
        User user = userService.getById(userVo.getStudentId());
        if (user == null) {
            return Result.fail("账号不存在!");
        }
        if (!user.getPassword().equals(SecureUtil.md5(userVo.getPassword()))) {
            log.info("用户密码错误，登录失败--------{}",user);
            return Result.fail("密码错误！");
        }
        if (user.getStatus() == 1) {
            log.info("用户被锁定，无法登录--------{}",user);
            return Result.fail("账号被锁定，无法登录");
        }
        String token = jwtUtils.CreateToken(userVo.getStudentId());
        response.setHeader("Authorization", token);
        response.setHeader("Access-control-Expose-Headers", "Authorization");

        return Result.success("登录成功", MapUtil.builder()
                .put("studentId", user.getStudentId())
                .put("username", user.getUserName())
                .put("picture", user.getPicture())
                .put("status",user.getStatus())
                .map()
        );
    }

    @RequiresAuthentication
    @GetMapping("/user/logout")
    public Result logout() {
        SecurityUtils.getSubject().logout();
        return Result.success("退出成功！");
    }

    @RequiresAuthentication
    @PutMapping("/user")
    public Result profile(@ApiParam("个人信息") @RequestBody User user) {
        log.info("信息修改------------{}",user);
        boolean result = userService.saveOrUpdate(user);
        if (result) {
            return Result.success("信息修改成功");
        }
        return Result.success("信息修改失败");
    }

    @ApiOperation("用户状态修改接口")
    @RequiresAuthentication
    @PutMapping("/user/status/{userId}")
    public Result userStatus(@PathVariable Long userId) {
        Result result = userService.updateStatus(userId);
        return result;
    }

    @RequiresAuthentication
    @DeleteMapping("/user/{studentId}")
    public Result remove(@PathVariable Long studentId) {
        User user = userService.getById(studentId);
        Integer status = user.getStatus();
        if (status == 1) {
            return Result.fail("账户被锁定，不允许注销");
        }
        boolean b = userService.removeById(studentId);
        if (b) {
            return Result.success("账号注销成功");
        }
        return Result.fail("账号注销失败");
    }


    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file,
                         HttpServletRequest request) {
        Result result = userService.setPicture(file, request);
        return result;
    }


    // 需要用户或管理员角色
    @RequiresRoles(value = {"user", "root"}, logical = Logical.OR)
    @GetMapping("/roles")
    public Result Roles() {
        SecurityUtils.getSubject().logout();
        return Result.success("角色界面！");
    }

    // 需要A权限或者B权限
    @RequiresPermissions(value = {"A", "B"}, logical = Logical.OR)
    @GetMapping("/perms")
    public Result perms() {
        SecurityUtils.getSubject().logout();
        return Result.success("权限界面");
    }

}



