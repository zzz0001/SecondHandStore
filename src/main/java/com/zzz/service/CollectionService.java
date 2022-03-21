package com.zzz.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzz.Util.Result;
import com.zzz.pojo.entity.Collection;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 收藏表 服务类
 * </p>
 *
 * @author zzz
 * @since 2022-03-02
 */
public interface CollectionService extends IService<Collection> {

    Result saveCollection(Collection collection);

    Result getCollection(Long studentId, Page<Collection> page);
}
