package com.zzz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzz.Util.Result;
import com.zzz.pojo.entity.Orders;
import com.zzz.pojo.entity.vo.OrderListVo;
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

    Result paymentList(OrderListVo orderListVo);

    Result delivery(Long orderId);

    Result receive(Long orderId);

    Result returnGoods(Long orderId);

    Result saveOrder(Orders order);

    Result getByStudentId(Long studentId);

    Result getByStatus(Long studentId,Integer status);

    Result getByStoreIdAndStatus(Long studentId, Integer status);
}
