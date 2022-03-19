package com.zzz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zzz.Util.Result;
import com.zzz.pojo.entity.Collection;
import com.zzz.mapper.CollectionMapper;
import com.zzz.service.CollectionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 收藏表 服务实现类
 * </p>
 *
 * @author zzz
 * @since 2022-03-02
 */
@Service
public class CollectionServiceImpl extends ServiceImpl<CollectionMapper, Collection> implements CollectionService {

    @Override
    public Result saveCollection(Collection collection) {
        Long goodsId = collection.getGoodsId();
        Long studentId = collection.getStudentId();
        QueryWrapper<Collection> wrapper = new QueryWrapper<Collection>().eq("student_id", studentId).eq("goods_id", goodsId);
        Collection collection1 = baseMapper.selectOne(wrapper);
        if (collection1 != null){
            return Result.fail(415,"商品已添加到收藏,不能重复添加");
        }
        int insert = baseMapper.insert(collection);
        if (insert == 1){
            return Result.success("商品收藏成功");
        }
        return Result.fail("商品收藏失败");
    }
}
