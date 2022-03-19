package com.zzz.controller;


import com.zzz.Util.JwtUtils;
import com.zzz.Util.Result;
import com.zzz.pojo.entity.Orders;
import com.zzz.pojo.entity.vo.OrderVo;
import com.zzz.service.AccountService;
import com.zzz.service.OrdersService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author zzz
 * @since 2022-03-02
 */
@Slf4j
@RestController
public class OrdersController {

    @Resource
    private OrdersService orderService;

    @Resource
    private AccountService accountService;

    @Resource
    private JwtUtils jwtUtils;

    @ApiOperation(value = "订单生成接口")
    @RequiresAuthentication
    @PostMapping("/order")
    public Result add(@RequestBody Orders order,
                      HttpServletRequest request) {
        boolean lock = accountService.isLock(request);
        if (lock) {
            return Result.fail("账户被锁定，不允许下单");
        }
        Result result = orderService.saveOrder(order);
        return result;
    }

    @RequiresAuthentication
    @GetMapping("/order/{orderId}")
    public Result getOrder(@PathVariable Long orderId) {
        Orders order = orderService.getById(orderId);
        if (order == null) {
            return Result.fail("订单号不存在");
        }
        return Result.success(order);
    }

    @RequiresAuthentication
    @GetMapping("/orderList/{studentId}")
    public Result getOrderList(@PathVariable Long studentId) {
        Result result = orderService.getByStudent(studentId);
        return result;
    }

    @ApiOperation(value = "付款接口")
    @RequiresAuthentication
    @PutMapping("/order")
    public Result transfer(@RequestBody OrderVo orderVo) {
        Result result = orderService.payment(orderVo);
        return result;
    }

    @ApiOperation(value = "发货接口")
    @RequiresAuthentication
    @PutMapping("/order/deliver/{orderId}")
    public Result deliver(@PathVariable Long orderId) {
        Result result = orderService.delivery(orderId);
        return result;
    }

    @ApiOperation(value = "收货接口")
    @RequiresAuthentication
    @PutMapping("/order/receive/{orderId}")
    public Result receive(@PathVariable Long orderId) {
       Result result = orderService.receive(orderId);
       return result;
    }

    @ApiOperation(value = "退货接口")
    @RequiresAuthentication
    @PutMapping("/order/return/{orderId}")
    public Result returnGoods(@PathVariable Long orderId) {
        Result result = orderService.returnGoods(orderId);
        return result;
    }

    @RequiresAuthentication
    @DeleteMapping("/order/{orderId}")
    public Result remove(@PathVariable Long orderId,
                         HttpServletRequest request) {
        boolean lock = accountService.isLock(request);
        if (lock) {
            return Result.fail("账户被锁定，不允许删除订单");
        }
        Orders orders = orderService.getById(orderId);
        Integer status = orders.getOrderStatus();
        if (status == 1 || status == 2) {
            return Result.fail("订单未确认收货，不允许删除订单");
        }
        boolean b = orderService.removeById(orderId);
        if (b) {
            return Result.success("订单删除成功");
        }
        return Result.fail("订单删除失败");
    }

}


