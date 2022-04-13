package com.zzz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzz.Util.Result;
import com.zzz.mapper.GoodsMapper;
import com.zzz.mapper.StoreMapper;
import com.zzz.mapper.UserMapper;
import com.zzz.pojo.entity.Goods;
import com.zzz.pojo.entity.Store;
import com.zzz.pojo.entity.User;
import com.zzz.service.StoreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 店铺表 服务实现类
 * </p>
 *
 * @author zzz
 * @since 2022-03-02
 */
@Service
public class StoreServiceImpl extends ServiceImpl<StoreMapper, Store> implements StoreService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private GoodsMapper goodsMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result saveStore(Store store, User user) {
        store.setStoreEvaluation(0F);
        store.setDeleted(false);
        int insert = baseMapper.insert(store);
        user.setStatus(3);
        int update = userMapper.updateById(user);
        if (insert == 1 && update == 1){
            return Result.success(store);
        }
        return Result.fail("店铺开通失败");
    }

    @Override
    public Result getByGoodsId(Long goodsId) {
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null){
            return Result.fail("没有找到该店铺");
        }
        Long studentId = goods.getStudentId();
        QueryWrapper<Store> queryWrapper = new QueryWrapper<Store>().eq("student_id", studentId);
        Store store = baseMapper.selectOne(queryWrapper);
        if (store == null){
            return Result.fail("没有找到该店铺");
        }
        return Result.success(store);
    }
}
