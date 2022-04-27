package com.zzz.controller;


import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zzz.Util.JwtUtils;
import com.zzz.Util.Result;
import com.zzz.pojo.entity.Root;
import com.zzz.service.RootService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 管理员表 前端控制器
 * </p>
 *
 * @author zzz
 * @since 2022-03-29
 */
@Slf4j
@RestController
public class RootController {

    @Resource
    private RootService rootService;

    @Resource
    private JwtUtils jwtUtils;

    @PostMapping("/root/login")
    public Result Login(@RequestBody Root root,
                        HttpServletResponse response){
        log.info("管理员进行登录---------{}",root);
        Root root2 = rootService.getOne(new QueryWrapper<Root>().eq("user_name",root.getUserName()));
        if (root2 == null) {
            return Result.fail("账号不存在!");
        }
        if (!root2.getPassword().equals(root.getPassword())) {
            log.info("用户密码错误，登录失败--------{}",root);
            return Result.fail("密码错误！");
        }
        String token = jwtUtils.CreateToken(root2.getStudentId());
        response.setHeader("Authorization", token);
        response.setHeader("Access-control-Expose-Headers", "Authorization");
        return Result.success("登录成功", MapUtil.builder()
                .put("studentId", root2.getStudentId())
                .put("username", root2.getUserName())
                .put("picture","/rootPicture.jpeg")
                .map());
    }

    @RequiresAuthentication
    @GetMapping("/root/logout")
    public Result logout() {
        SecurityUtils.getSubject().logout();
        return Result.success("退出成功！");
    }


}

