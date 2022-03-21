package com.zzz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzz.Util.Result;
import com.zzz.mapper.CollectionMapper;
import com.zzz.mapper.GoodsMapper;
import com.zzz.mapper.StoreMapper;
import com.zzz.pojo.entity.Collection;
import com.zzz.pojo.entity.Goods;
import com.zzz.pojo.entity.Store;
import com.zzz.service.CollectionService;
import com.zzz.service.ImageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    @Resource
    private GoodsMapper goodsMapper;

    @Resource
    private StoreMapper storeMapper;

    @Resource
    private ImageService imageService;

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

    @Override
    public Result getCollection(Long studentId, Page<Collection> page) {
        QueryWrapper<Collection> queryWrapper = new QueryWrapper<Collection>().eq("student_id", studentId).orderByDesc("collection_id");
        Page<Collection> collectionPage = baseMapper.selectPage(page, queryWrapper);
        ArrayList<Object> collectionList = new ArrayList<>();
        List<Collection> collections = collectionPage.getRecords();
        collections.forEach(collection -> {
            Long goodsId = collection.getGoodsId();
            Goods goods = goodsMapper.selectById(goodsId);
            Long goodsStudentId = goods.getStudentId();
            QueryWrapper<Store> wrapper = new QueryWrapper<Store>().eq("student_id", goodsStudentId);
            Store store = storeMapper.selectOne(wrapper);
            List<String> images = imageService.getImagesByGoodsId(goodsId);
            HashMap<String, Object> map = new HashMap<>();
            map.put("goods",goods);
            map.put("store",store);
            map.put("images",images);
            map.put("collection",collection);
            collectionList.add(map);
        });
        HashMap<String, Object> result = new HashMap<>();
        result.put("collection",collectionList);
        result.put("page",collectionPage);
        return Result.success(result);
    }
}
