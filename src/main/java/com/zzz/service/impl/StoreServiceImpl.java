package com.zzz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzz.Util.Result;
import com.zzz.mapper.StoreMapper;
import com.zzz.mapper.UserMapper;
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
}
