package com.zzz.service;

import com.zzz.Util.Result;
import com.zzz.pojo.entity.Store;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zzz.pojo.entity.User;

/**
 * <p>
 * 店铺表 服务类
 * </p>
 *
 * @author zzz
 * @since 2022-03-02
 */
public interface StoreService extends IService<Store> {

    Result saveStore(Store store, User user);

}
