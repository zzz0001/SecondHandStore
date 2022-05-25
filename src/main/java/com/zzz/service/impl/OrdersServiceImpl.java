package com.zzz.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzz.Util.MessageUtils;
import com.zzz.Util.Result;
import com.zzz.exception.BusinessException;
import com.zzz.mapper.*;
import com.zzz.pojo.entity.*;
import com.zzz.pojo.vo.OrderListVo;
import com.zzz.pojo.vo.OrderVo;
import com.zzz.service.AccountService;
import com.zzz.service.ImageService;
import com.zzz.service.OrdersService;
import com.zzz.socket.WebSocket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.retry.RetryException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
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

    @Resource
    private UserMapper userMapper;

    @Resource
    private ExpenseMapper expenseMapper;

    @Resource
    private WebSocket webSocket;

    @Override
    public Result saveOrder(Orders order) {
        Long goodsId = order.getGoodsId();
        Long studentId = order.getStudentId();
        QueryWrapper<Orders> wrapper = new QueryWrapper<Orders>().eq("student_id", studentId).eq("goods_id", goodsId).eq("order_status", 0);
        Orders orders = baseMapper.selectOne(wrapper);
        if (orders != null) {
            return Result.fail(415, "商品已添加到购物车,不能重复添加", orders.getOrderId());
        }
        Goods goods = goodsMapper.selectById(goodsId);
        Integer goodsInventory = goods.getGoodsInventory();
        if (goodsInventory < order.getGoodsNum()) {
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

    private Result getResultByOrder(QueryWrapper<Orders> wrapper) {
        List<Orders> orders = baseMapper.selectList(wrapper);
        ArrayList<Object> result = new ArrayList<>();
        orders.forEach(order -> {
            HashMap<String, Object> map = new HashMap<>();
            List<String> images = imageService.getImagesByGoodsId(order.getGoodsId());
            Goods goods = goodsMapper.selectById(order.getGoodsId());
            Store store = storeMapper.selectById(order.getStoreId());
            map.put("order", order);
            map.put("images", images);
            map.put("goods", goods);
            map.put("store", store);
            result.add(map);
        });
        return Result.success(result);
    }

    private Result getResultByStoreId(QueryWrapper<Orders> wrapper) {
        List<Orders> orders = baseMapper.selectList(wrapper);
        ArrayList<Object> result = new ArrayList<>();
        orders.forEach(order -> {
            HashMap<String, Object> map = new HashMap<>();
            List<String> images = imageService.getImagesByGoodsId(order.getGoodsId());
            Goods goods = goodsMapper.selectById(order.getGoodsId());
            User user = userMapper.selectById(order.getStudentId());
            map.put("order", order);
            map.put("images", images);
            map.put("goods", goods);
            map.put("user", user);
            result.add(map);
        });
        return Result.success(result);
    }

    @Override
    public Result getByStudentId(Long studentId) {
        QueryWrapper<Orders> wrapper = new QueryWrapper<Orders>().eq("student_id", studentId).orderByDesc("create_time");
        return getResultByOrder(wrapper);
    }


    @Override
    public Result getByStoreIdAndStatus(Long studentId, Integer status) {
        QueryWrapper<Store> queryWrapper = new QueryWrapper<Store>().eq("student_id", studentId);
        Store store = storeMapper.selectOne(queryWrapper);
        Long storeId = store.getStoreId();
        QueryWrapper<Orders> wrapper = new QueryWrapper<Orders>().eq("store_id", storeId).eq("order_status", status).orderByDesc("order_date");
        return getResultByStoreId(wrapper);
    }

    @Override
    public Result requestReturn(Long orderId) {
        Orders orders = baseMapper.selectById(orderId);
        orders.setOrderStatus(5);
        int update = baseMapper.updateById(orders);
        if (update == 1) {
            try {
                Store store = storeMapper.selectById(orders.getStoreId());
                String message = MessageUtils.getMessage(3, "1");
                webSocket.sendMessage(store.getStudentId().toString(), message);
                return Result.success("退货申请成功");
            } catch (RedisConnectionFailureException e) {
                log.error("Redis连接失败");
                return Result.success("退货申请成功");
            }
        }
        return Result.fail("退货申请失败");
    }

    @Override
    public Result getBySingleStatus(Long studentId, Integer status) {
        Store store = storeMapper.selectOne(new QueryWrapper<Store>().eq("student_id", studentId));
        Long storeId = store.getStoreId();
        QueryWrapper<Orders> wrapper = null;
        if (status < 5) {
            wrapper = new QueryWrapper<Orders>().eq("store_id", storeId).eq("order_status", status).orderByDesc("create_time");
        } else {
            wrapper = new QueryWrapper<Orders>().eq("store_id", storeId).ge("order_status", status).orderByDesc("create_time");
        }
        return getResultByStoreId(wrapper);
    }

    @Override
    public Result getByOrderId(Long orderId) {
        Orders order = baseMapper.selectById(orderId);
        if (order == null) {
            return Result.fail("订单号不存在");
        }
        HashMap<String, Object> map = new HashMap<>();
        List<String> images = imageService.getImagesByGoodsId(order.getGoodsId());
        Goods goods = goodsMapper.selectById(order.getGoodsId());
        Store store = storeMapper.selectById(order.getStoreId());
        map.put("order", order);
        map.put("images", images);
        map.put("goods", goods);
        map.put("store", store);
        return Result.success(map);
    }

    @Override
    public Result urge(Long orderId) {
        Orders orders = baseMapper.selectById(orderId);
        orders.setUrgent(1);
        int update = baseMapper.updateById(orders);
        if (update == 1) {
            Store store = storeMapper.selectById(orders.getStoreId());
            try {
                String message = MessageUtils.getMessage(2, "1");
                webSocket.sendMessage(store.getStudentId().toString(), message);
            } catch (RedisConnectionFailureException e) {
                log.error("Redis连接失败");
            }
        }
        return Result.success(null);
    }

    @Override
    public Result getPageByStatus(Integer page, Integer status) {
        Page<Orders> ordersPage = new Page<>(page, 10);
        QueryWrapper<Orders> wrapper = null;
        if (status < 4) {
            wrapper = new QueryWrapper<Orders>().eq("order_status", status).orderByDesc("order_id");
        } else {
            wrapper = new QueryWrapper<Orders>().ge("order_status", status).orderByDesc("order_id");
        }
        Page<Orders> resultPage = baseMapper.selectPage(ordersPage, wrapper);
        List<Orders> orders = resultPage.getRecords();
        ArrayList<Object> resultValue = new ArrayList<>();
        orders.forEach(order -> {
            HashMap<String, Object> map = new HashMap<>();
            List<String> images = imageService.getImagesByGoodsId(order.getGoodsId());
            Goods goods = goodsMapper.selectById(order.getGoodsId());
            Store store = storeMapper.selectById(order.getStoreId());
            map.put("order", order);
            map.put("images", images);
            map.put("goods", goods);
            map.put("store", store);
            resultValue.add(map);
        });
        HashMap<String, Object> result = new HashMap<>();
        resultPage.setRecords(null);
        result.put("page", resultPage);
        result.put("orders", resultValue);
        return Result.success(result);
    }

    @Override
    public Result getByStatus(Long studentId, Integer status) {
        QueryWrapper<Orders> wrapper = null;
        if (status < 4) {
            wrapper = new QueryWrapper<Orders>().eq("student_id", studentId).eq("order_status", status).orderByDesc("create_time");
        } else {
            wrapper = new QueryWrapper<Orders>().eq("student_id", studentId).ge("order_status", status).orderByDesc("create_time");
        }
        return getResultByOrder(wrapper);
    }


    @Transactional(rollbackFor = Exception.class)
    @Retryable(value = RetryException.class, maxAttempts = 5, backoff = @Backoff(delay = 100L, multiplier = 2))
    @Override
    public Result payment(OrderVo orderVo) {
        String password = orderVo.getPassword();
        Long orderId = orderVo.getOrderId();
        Orders order = baseMapper.selectById(orderId);
        Long goodsId = order.getGoodsId();
        Goods goods = goodsMapper.selectById(goodsId);
        Integer goodsInventory = goods.getGoodsInventory();
        if (goodsInventory < order.getGoodsNum()) {
            return Result.fail("商品库存不足");
        }
        Long sellerId = goods.getStudentId();
        boolean lock = accountService.isLock(sellerId);
        if (lock) {
            throw new BusinessException("商家账户被锁定，不可购买该店铺商品");
        }
        Long studentId = order.getStudentId();
        Account account = accountService.getById(studentId);
        if (!account.getPassword().equals(SecureUtil.md5(password))) {
            return Result.fail("密码错误");
        }
        Double price = order.getTotalPrice();
        try {
            accountService.transfer(studentId, -999999L, price);
        } catch (BusinessException e) {
            throw e;
        }
        log.info("账户:{} 支付了 {}", studentId, price);
        goods.setGoodsInventory(goodsInventory - order.getGoodsNum());
        int update = goodsMapper.updateById(goods);
        if (update != 1) {
            throw new RetryException("商品库存更新失败");
        }
        log.info("商品:{} 销售了 {}", goodsId, order.getGoodsNum());
        // 将订单状态改为已付款
        order.setOrderStatus(1);
        order.setOrderDate(LocalDateTime.now());
        int i = baseMapper.updateById(order);
        if (i == 1 && update == 1) {
            Expense expense = new Expense();
            expense.setCost(price);
            expense.setStatus(0);
            expense.setStudentId(studentId);
            expense.setReceiveId(sellerId);
            expense.setOrderId(orderId);
            expenseMapper.insert(expense);
            return Result.success("付款成功", sellerId);
        }
        throw new BusinessException("付款失败");
    }

    @Override
    public Result paymentList(OrderListVo orderListVo) {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<Long> orderIdList = orderListVo.getOrderIdList();
        orderIdList.forEach(orderId -> {
            OrderVo orderVo = new OrderVo();
            orderVo.setOrderId(orderId);
            orderVo.setPassword(orderListVo.getPassword());
            Orders orders = baseMapper.selectById(orderId);
            Result payment;
            try {
                payment = payment(orderVo);
                if (payment.getCode() != 200) {
                    Goods goods = goodsMapper.selectById(orders.getGoodsId());
                    String message = "商品：" + goods.getGoodsName() + " 支付失败, 原因：" + payment.getMessage();
                    result.add(message);
                } else {
                    String message = MessageUtils.getMessage(1, "1");
                    Store store = storeMapper.selectById(orders.getStoreId());
                    webSocket.sendMessage(store.getStudentId().toString(), message);
                }
            } catch (RedisConnectionFailureException e) {
                log.error("redis连接连接错误");
            } catch (Exception e) {
                Goods goods = goodsMapper.selectById(orders.getGoodsId());
                String message = "商品：" + goods.getGoodsName() + " 支付失败, 原因：" + e.getMessage();
                result.add(message);
            }
        });
        return Result.success(result);
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
            Expense expense = new Expense();
            expense.setCost(price);
            expense.setStatus(2);
            expense.setStudentId(studentId);
            expense.setReceiveId(order.getStudentId());
            expense.setOrderId(orderId);
            expenseMapper.insert(expense);
            return Result.success("收货成功");
        }
        throw new BusinessException("收货失败");
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
        log.info("商家{} 向 买家{} 转账 {}", sell, buy, price);
        // 将订单状态改为已退货
        order.setOrderStatus(6);
        order.setReturnDate(LocalDateTime.now());
        Long goodsId = order.getGoodsId();
        Goods goods = goodsMapper.selectById(goodsId);
        goods.setGoodsInventory(goods.getGoodsInventory() + order.getGoodsNum());
        int update = goodsMapper.updateById(goods);
        int b = baseMapper.updateById(order);
        if (b == 1 && update == 1) {
            Expense expense = new Expense();
            expense.setCost(price);
            expense.setStatus(0);
            expense.setStudentId(sell);
            expense.setReceiveId(buy);
            expense.setOrderId(orderId);
            expenseMapper.insert(expense);
            Expense expense2 = new Expense();
            expense2.setCost(price);
            expense2.setStatus(2);
            expense2.setStudentId(buy);
            expense2.setReceiveId(sell);
            expense2.setOrderId(orderId);
            expenseMapper.insert(expense2);
            return Result.success("退货成功");
        }
        throw new BusinessException("退货失败");
    }


}
