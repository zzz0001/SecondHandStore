package com.zzz.service;

import com.zzz.Util.Result;
import com.zzz.pojo.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author zzz
 * @since 2022-03-03
 */
public interface OrdersService extends IService<Orders> {

    Result payment(Long orderId);

    Result delivery(Long orderId);

    Result receive(Long orderId);

    Result returnGoods(Long orderId);

    Result saveOrder(Orders order);
}
