package com.zzz.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zzz.Util.JwtUtils;
import com.zzz.Util.Result;
import com.zzz.pojo.entity.Store;
import com.zzz.pojo.entity.User;
import com.zzz.service.AccountService;
import com.zzz.service.StoreService;
import com.zzz.service.UserService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 店铺表 前端控制器
 * </p>
 *
 * @author zzz
 * @since 2022-03-02
 */
@RestController
public class StoreController {

    @Resource
    private StoreService storeService;

    @Resource
    private UserService userService;

    @Resource
    private AccountService accountService;

    @Resource
    private JwtUtils jwtUtils;

    @RequiresAuthentication
    @PostMapping("/store")
    public Result addStore(@RequestBody Store store){
        Long studentId = store.getStudentId();
        User user = userService.getById(studentId);
        Integer status = user.getStatus();
        if (status == 1){
            return Result.fail("账号已被冻结，不允许开通店铺");
        }else if(status == 0){
            return Result.fail("账号没有开通账户，请先开通账户");
        }
        Result result = storeService.saveStore(store,user);
        return result;

    }

    @GetMapping("/store/{storeId}")
    public Result getStore(@PathVariable Long storeId){
        Store store = storeService.getById(storeId);
        if (store == null){
            return Result.fail("不存在该店铺");
        }
        return Result.success(store);
    }

    @GetMapping("/storeByStudentId/{studentId}")
    public Result getStoreByStudentId(@PathVariable Long studentId){
        QueryWrapper<Store> queryWrapper = new QueryWrapper<Store>().eq("student_id", studentId);
        Store store = storeService.getOne(queryWrapper);
        if (store == null){
            return Result.success(null);
        }
        return Result.success(store);
    }

    @RequiresAuthentication
    @PutMapping("/store")
    public Result changeStore(@RequestBody Store store,
                              HttpServletRequest request){
        boolean lock = accountService.isLock(request);
        if (lock){
            return Result.fail("账户被锁定，不允许操作");
        }
        boolean b = storeService.updateById(store);
        if (b) {
            return Result.success(store);
        }
        return Result.fail("店铺信息修改失败");
    }

    @RequiresAuthentication
    @DeleteMapping("/store")
    public Result remove(@RequestBody Store store,
                         HttpServletRequest request){
        boolean lock = accountService.isLock(request);
        if (lock){
            return Result.fail("账户被锁定，不允许操作");
        }
        boolean b = storeService.removeById(store.getStoreId());
        if (b) {
            return Result.success("店铺关闭成功");
        }
        return Result.fail("店铺关闭失败");
    }

}

