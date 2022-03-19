package com.zzz.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzz.Util.Result;
import com.zzz.mapper.GoodsMapper;
import com.zzz.mapper.OrdersMapper;
import com.zzz.mapper.StoreMapper;
import com.zzz.pojo.entity.Account;
import com.zzz.pojo.entity.Goods;
import com.zzz.pojo.entity.Orders;
import com.zzz.pojo.entity.Store;
import com.zzz.pojo.entity.vo.OrderVo;
import com.zzz.service.AccountService;
import com.zzz.service.ImageService;
import com.zzz.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    @Resource
    private ImageService imageService;

    @Override
    public Result saveOrder(Orders order) {
        Long goodsId = order.getGoodsId();
        Long studentId = order.getStudentId();
        QueryWrapper<Orders> wrapper = new QueryWrapper<Orders>().eq("student_id",studentId).eq("goods_id",goodsId);
        Orders orders = baseMapper.selectOne(wrapper);
        if (orders != null){
            return Result.fail(415,"商品已添加到购物车,不能重复添加",orders.getOrderId());
        }
        Goods goods = goodsMapper.selectById(goodsId);
        Integer goodsInventory = goods.getGoodsInventory();
        if (goodsInventory<order.getGoodsNum()){
            return Result.fail("商品库存不足，下单失败");
        }
        QueryWrapper<Store> queryWrapper = new QueryWrapper<Store>().eq("student_id", order.getStoreId());
        Store store = storeMapper.selectOne(queryWrapper);
        order.setStoreId(store.getStoreId());
        order.setOrderStatus(0);
        order.setDeleted(false);
        int b = baseMapper.insert(order);
        if (b == 1) {
            return Result.success(order.getOrderId());
        }
        return Result.fail("订单生成失败");
    }

    @Override
    public Result getByStudent(Long studentId) {
        QueryWrapper<Orders> wrapper = new QueryWrapper<Orders>().eq("student_id", studentId);
        List<Orders> orders = baseMapper.selectList(wrapper);
        ArrayList<Object> result = new ArrayList<>();
        orders.forEach(order ->{
            HashMap<String, Object> map = new HashMap<>();
            List<String> images = imageService.getImagesByGoodsId(order.getGoodsId());
            Goods goods = goodsMapper.selectById(order.getGoodsId());
            Store store = storeMapper.selectById(order.getStoreId());
            map.put("order",order);
            map.put("images",images);
            map.put("goods",goods);
            map.put("store",store);
            result.add(map);
        });
        return Result.success(result);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result payment(OrderVo orderVo) {
        String password = orderVo.getPassword();
        Long orderId = orderVo.getOrderId();
        Orders order = baseMapper.selectById(orderId);
        Long goodsId = order.getGoodsId();
        Goods goods = goodsMapper.selectById(goodsId);
        Integer goodsInventory = goods.getGoodsInventory();
        if (goodsInventory < order.getGoodsNum()) {
            return Result.fail("商品库存不足,付款失败");
        }
        Long studentId = order.getStudentId();
        Account account = accountService.getById(studentId);
        if (!account.getPassword().equals(SecureUtil.md5(password))){
            return Result.fail("密码错误，支付失败");
        }
        Double price = order.getTotalPrice();
        accountService.transfer(studentId, -999999L, price);
        log.info("账户:{} 支付了 {}", studentId, price);
        goods.setGoodsInventory(goodsInventory - order.getGoodsNum());
        int update = goodsMapper.updateById(goods);
        log.info("商品:{} 销售了 {}", goodsId, order.getGoodsNum());
        // 将订单状态改为已付款
        order.setOrderStatus(1);
        order.setOrderDate(LocalDateTime.now());
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
        order.setDeliveryDate(LocalDateTime.now());
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
        order.setReceiveDate(LocalDateTime.now());
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
        order.setReturnDate(LocalDateTime.now());
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
