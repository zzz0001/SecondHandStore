package com.zzz.service;

import com.zzz.Util.Result;
import com.zzz.pojo.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zzz.pojo.entity.vo.OrderVo;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author zzz
 * @since 2022-03-03
 */
public interface OrdersService extends IService<Orders> {

    Result payment(OrderVo orderVo);

    Result delivery(Long orderId);

    Result receive(Long orderId);

    Result returnGoods(Long orderId);

    Result saveOrder(Orders order);

    Result getByStudent(Long studentId);
}
