package com.zzz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzz.Util.Result;
import com.zzz.mapper.GoodsMapper;
import com.zzz.mapper.OrdersMapper;
import com.zzz.mapper.StoreMapper;
import com.zzz.pojo.entity.Goods;
import com.zzz.pojo.entity.Orders;
import com.zzz.pojo.entity.Store;
import com.zzz.service.AccountService;
import com.zzz.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author zzz
 * @since 2022-03-03
 */
@Slf4j
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Resource
    private AccountService accountService;

    @Resource
    private StoreMapper storeMapper;

    @Resource
    private GoodsMapper goodsMapper;

    @Override
    public Result saveOrder(Orders order) {
        Long goodsId = order.getGoodsId();
        Goods goods = goodsMapper.selectById(goodsId);
        Integer goodsInventory = goods.getGoodsInventory();
        if (goodsInventory<order.getGoodsNum()){
            return Result.fail("商品库存不足，下单失败");
        }
        order.setOrderStatus(0);
        order.setDeleted(false);
        int b = baseMapper.insert(order);
        if (b == 1) {
            return Result.success("成功生成订单");
        }
        return Result.fail("订单生成失败");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result payment(Long orderId) {
        Orders order = baseMapper.selectById(orderId);
        Long goodsId = order.getGoodsId();
        Goods goods = goodsMapper.selectById(goodsId);
        Integer goodsInventory = goods.getGoodsInventory();
        if (goodsInventory < order.getGoodsNum()) {
            return Result.fail("商品库存不足,付款失败");
        }
        Long studentId = order.getStudentId();
        Double price = order.getTotalPrice();
        accountService.transfer(studentId, -999999L, price);
        log.info("账户:{} 支付了 {}", studentId, price);
        goods.setGoodsInventory(goodsInventory - order.getGoodsNum());
        int update = goodsMapper.updateById(goods);
        log.info("商品:{} 销售了 {}", goodsId, order.getGoodsNum());
        // 将订单状态改为已付款
        order.setOrderStatus(1);
        int i = baseMapper.updateById(order);
        if (i == 1 && update == 1) {
            return Result.success("付款成功");
        }
        return Result.fail("付款失败");
    }

    @Override
    public Result delivery(Long orderId) {
        Orders order = baseMapper.selectById(orderId);
        // 将订单状态改为已发货
        order.setOrderStatus(2);
        int i = baseMapper.updateById(order);
        if (i == 1) {
            return Result.success("发货成功");
        }
        return Result.fail("发货失败");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result receive(Long orderId) {
        Orders order = baseMapper.selectById(orderId);
        Long storeId = order.getStoreId();
        Double price = order.getTotalPrice();
        Store store = storeMapper.selectById(storeId);
        Long studentId = store.getStudentId();
        accountService.transfer(-999999L, studentId, price);
        log.info("商家{} 到账 {}", studentId, price);
        // 将订单状态改为已收货
        order.setOrderStatus(3);
        int i = baseMapper.updateById(order);
        if (i == 1) {
            return Result.success("收货成功");
        }
        return Result.fail("收货失败");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result returnGoods(Long orderId) {
        Orders order = baseMapper.selectById(orderId);
        Long buy = order.getStudentId();
        Double price = order.getTotalPrice();
        Long storeId = order.getStoreId();
        Store store = storeMapper.selectById(storeId);
        Long sell = store.getStudentId();
        accountService.transfer(sell, buy, price);
        log.info("商家{} 向买家{} 转账 {}", sell, buy, price);
        // 将订单状态改为已退货
        order.setOrderStatus(4);
        Long goodsId = order.getGoodsId();
        Goods goods = goodsMapper.selectById(goodsId);
        goods.setGoodsInventory(goods.getGoodsInventory() + order.getGoodsNum());
        int update = goodsMapper.updateById(goods);
        int b = baseMapper.updateById(order);
        if (b == 1 && update == 1) {
            return Result.success("退货成功");
        }
        return Result.fail("退货失败");
    }


}
