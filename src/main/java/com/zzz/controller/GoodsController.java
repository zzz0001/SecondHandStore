package com.zzz.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzz.Util.JwtUtils;
import com.zzz.Util.Result;
import com.zzz.pojo.entity.Account;
import com.zzz.pojo.entity.Goods;
import com.zzz.pojo.entity.vo.GoodsVO;
import com.zzz.service.AccountService;
import com.zzz.service.GoodsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 商品表	 前端控制器
 * </p>
 *
 * @author zzz
 * @since 2022-03-02
 */
@Slf4j
@RestController
public class GoodsController {

    @Resource
    private GoodsService goodsService;

    @Resource
    private AccountService accountService;

    @Resource
    private JwtUtils jwtUtils;

    @RequiresAuthentication
    @PostMapping(value = "/goods")
    public Result addGoods(@RequestBody GoodsVO goodsVO,
                           HttpServletRequest request) {
        Long studentId = jwtUtils.getStudentId(request);
        Account account = accountService.getById(studentId);
        if (account.getStudentId() == 1) {
            return Result.fail("账户被锁定，不允许操作");
        }
        Boolean save = goodsService.saveOrUpdateGoodsVO(goodsVO,studentId);
        if (save){
            return Result.success("操作成功");
        }
        return Result.fail("操作失败");
    }

    @GetMapping("/goodsPage/{page}")
    public Result goodsPage(@PathVariable Integer page){
        Page<Goods> goodsPage = new Page<>(page,2);
        Page<Goods> goods = goodsService.page(goodsPage, new QueryWrapper<Goods>().orderByDesc("goods_id"));
        return Result.success(goods);
    }

    @GetMapping("/goods/{goodsId}")
    public Result getGoods(@PathVariable Long goodsId) {
        Result result = goodsService.getGoods(goodsId);
        return result;
    }

    @GetMapping("/goodsByStudentId/{studentId}")
    public Result getGoodsByStudentId(@PathVariable Long studentId) {
        Result result = goodsService.getGoodsByStudentId(studentId);
        return result;
    }

    @RequiresAuthentication
    @DeleteMapping("/goods/{goodsId}")
    public Result remove(@PathVariable Long goodsId,
                         HttpServletRequest request) {
        boolean lock = accountService.isLock(request);
        if (lock){
            return Result.fail("账户被锁定，不允许下架商品");
        }
        Result result = goodsService.removeGoods(goodsId);
        return result;
    }

    @RequiresAuthentication
    @PutMapping("/goods/inventory")
    public Result inventory(@RequestParam("goodsId") Long goodsId,
                            @RequestParam("inventory") Integer inventory){
        Result result = goodsService.addInventory(goodsId,inventory);
        return result;
    }


}

