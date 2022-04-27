package com.zzz.controller;


import com.zzz.Util.JwtUtils;
import com.zzz.Util.MessageUtils;
import com.zzz.Util.Result;
import com.zzz.exception.BusinessException;
import com.zzz.pojo.entity.Orders;
import com.zzz.pojo.entity.User;
import com.zzz.pojo.vo.OrderListVo;
import com.zzz.pojo.vo.OrderVo;
import com.zzz.service.AccountService;
import com.zzz.service.OrdersService;
import com.zzz.service.UserService;
import com.zzz.socket.WebSocket;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.data.redis.RedisConnectionFailureException;
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
    private UserService userService;

    @Resource
    private JwtUtils jwtUtils;

    @Resource
    private WebSocket webSocket;

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
        Result result = orderService.getByOrderId(orderId);
        return result;
    }

    @RequiresAuthentication
    @GetMapping("/orderList/{studentId}")
    public Result getOrderList(@PathVariable Long studentId) {
        Result result = orderService.getByStudentId(studentId);
        return result;
    }

    @RequiresAuthentication
    @GetMapping("/orderListByStoreId/{status}")
    public Result getOrderListByStoreId(@PathVariable Integer status,
                                        HttpServletRequest request) {
        Long studentId = jwtUtils.getStudentId(request);
        Result result = orderService.getByStoreIdAndStatus(studentId,status);
        return result;
    }

    @RequiresAuthentication
    @GetMapping("/orderListByStatus/{status}")
    public Result getOrderListByStatus(@PathVariable Integer status,
                                       HttpServletRequest request) {
        Long studentId = jwtUtils.getStudentId(request);
        Result result = orderService.getByStatus(studentId,status);
        return result;
    }

    @RequiresAuthentication
    @GetMapping("/orderListByStudentIdAndStatus/{studentId}/{status}")
    public Result getOrderListByStudentIdAndStatus(@PathVariable Long studentId,
                                                   @PathVariable Integer status) {
        User user = userService.getById(studentId);
        if (user == null){
            return Result.fail("用户不存在");
        }
        Result result = orderService.getByStatus(studentId,status);
        return result;
    }


    @RequiresAuthentication
    @GetMapping("/orderBySingleStatus/{status}")
    public Result orderBySingleStatus(@PathVariable Integer status,
                                       HttpServletRequest request) {
        Long studentId = jwtUtils.getStudentId(request);
        Result result = orderService.getBySingleStatus(studentId,status);
        return result;
    }

    @ApiOperation(value = "付款接口")
    @RequiresAuthentication
    @PutMapping("/order")
    public Result transfer(@RequestBody OrderVo orderVo){

        Result result = null;
        try {
            result = orderService.payment(orderVo);
            if (result.getCode() == 200){
                String message = MessageUtils.getMessage(1, "1");
                webSocket.sendMessage(result.getData().toString(),message);
            }
            return result;
        } catch (RedisConnectionFailureException e){
            log.error("Redis连接失败");
            return result;
        } catch (BusinessException e){
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("库存异常");
            return Result.fail("商品库存不足,购买失败");
        }
    }

    @ApiOperation(value = "购物车付款接口")
    @RequiresAuthentication
    @PutMapping("/orders/payment")
    public Result paymentAll(@RequestBody OrderListVo orderListVo) {
        Result result = orderService.paymentList(orderListVo);
        return result;
    }

    @ApiOperation(value = "催发货接口")
    @RequiresAuthentication
    @PutMapping("/order/urge/{orderId}")
    public Result urge(@PathVariable Long orderId) {
        Result result = orderService.urge(orderId);
        return result;
    }

    @RequiresAuthentication
    @PutMapping("/order/delivery/{orderId}")
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

    @ApiOperation(value = "申请退货接口")
    @RequiresAuthentication
    @PutMapping("/order/requestReturn/{orderId}")
    public Result requestReturn(@PathVariable Long orderId) {
        Result result = orderService.requestReturn(orderId);
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


