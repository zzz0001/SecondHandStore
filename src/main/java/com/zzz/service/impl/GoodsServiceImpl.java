package com.zzz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzz.Util.Result;
import com.zzz.exception.BusinessException;
import com.zzz.mapper.GoodsMapper;
import com.zzz.mapper.OrdersMapper;
import com.zzz.pojo.entity.Goods;
import com.zzz.pojo.entity.Image;
import com.zzz.pojo.entity.Orders;
import com.zzz.pojo.vo.GoodsVO;
import com.zzz.service.CommentService;
import com.zzz.service.GoodsService;
import com.zzz.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 商品表	 服务实现类
 * </p>
 *
 * @author zzz
 * @since 2022-03-02
 */
@Slf4j
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {

    @Resource
    private ImageService imageService;

    @Resource
    private OrdersMapper ordersMapper;

    @Resource
    private CommentService commentService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result saveOrUpdateGoodsVO(GoodsVO goodsVO, Long studentId) {
        Goods goods = new Goods();
        BeanUtil.copyProperties(goodsVO, goods);
        goods.setStudentId(studentId);
        goods.setDeleted(false);
        int flag;
        // 商品ID为空，则代表是添加商品,否则为修改商品信息
        if (goodsVO.getGoodsId() == null) {
            flag = baseMapper.insert(goods);
        } else {
            flag = baseMapper.updateById(goods);
        }
        Long goodsId = goods.getGoodsId();
        List<String> imagesPath = goodsVO.getImages();
        if (imagesPath != null || imagesPath.size() != 0) {
            List<Image> images = new ArrayList<>();
            imagesPath.forEach(path -> {
                Image image = new Image();
                image.setGoodsId(goodsId);
                image.setImagePath(path);
                images.add(image);
            });
            boolean s2 = imageService.saveBatch(images);
        }
        if (flag == 1) {
            return Result.success("操作成功");
        }
        throw new BusinessException("操作失败");
    }

    @Override
    public Result getGoods(Long goodsId) {
        Goods goods = baseMapper.selectById(goodsId);
        if (goods == null) {
            return Result.fail("商品不存在");
        }
        List<Image> images = imageService.list(new QueryWrapper<Image>().eq("goods_id", goodsId));
        ArrayList<String> imagePath = new ArrayList<>();
        images.forEach(image -> {
            imagePath.add(image.getImagePath());
        });
        HashMap<String, Object> map = new HashMap<>();
        map.put("goods", goods);
        map.put("images", imagePath);
        return Result.success(map);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result removeGoods(Long goodsId) {
        QueryWrapper<Orders> wrapper = new QueryWrapper<Orders>().eq("goods_id", goodsId).eq("order_status", 1);
        Orders orders = ordersMapper.selectOne(wrapper);
        if (orders != null){
            return Result.fail("该商品存在未完结的订单，不允许下架");
        }
        imageService.removeByGoodsId(goodsId);
        commentService.removeCommentsByGoodsId(goodsId);
        int delete = baseMapper.deleteById(goodsId);
        if (delete == 1) {
            return Result.success("商品下架成功");
        }
        throw new BusinessException("商品下架失败");
    }

    @Retryable(value = RetryException.class, maxAttempts = 3, backoff = @Backoff(delay = 100L, multiplier = 2))
    @Override
    public Result addInventory(Long goodsId, Integer inventory) {
        Goods goods = baseMapper.selectById(goodsId);
        if (inventory < 0 && goods.getGoodsInventory() < Math.abs(inventory) ){
            goods.setGoodsInventory(0);
        }else{
            goods.setGoodsInventory(goods.getGoodsInventory() + inventory);
        }
        int update = baseMapper.updateById(goods);
        if (update != 1) {
            throw new RetryException("库存修改失败");
        }
        return Result.success("库存修改成功");
    }

    @Override
    public Result getGoodsByStudentId(Long studentId) {
        QueryWrapper<Goods> wrapper = new QueryWrapper<Goods>().eq("student_id", studentId);
        List<Goods> goodsList = baseMapper.selectList(wrapper);
        ArrayList<Object> arrayList = new ArrayList<>();
        goodsList.forEach(goods -> {
            HashMap<String, Object> map = new HashMap<>();
            QueryWrapper<Image> queryWrapper = new QueryWrapper<Image>().eq("goods_id", goods.getGoodsId());
            List<Image> images = imageService.list(queryWrapper);
            ArrayList<String> imagesPath = new ArrayList<>();
            images.forEach(image -> {
                imagesPath.add(image.getImagePath());
            });
            map.put("goods", goods);
            map.put("images", imagesPath);
            arrayList.add(map);
        });
        return Result.success(arrayList);
    }


    private Result getGoodsResult(Page<Goods> resultPage) {
        List<Goods> goodsList = resultPage.getRecords();
        ArrayList<Object> list = new ArrayList<>();
        HashMap<String, Object> result = new HashMap<>();
        goodsList.forEach(goods ->{
            List<String> images = imageService.getImagesByGoodsId(goods.getGoodsId());
            HashMap<String, Object> map = new HashMap<>();
            map.put("goods",goods);
            map.put("images",images);
            list.add(map);
        });
        resultPage.setRecords(null);
        result.put("goods",list);
        result.put("page",resultPage);
        return Result.success(result);
    }

    @Override
    public Result getGoodsByPage(Integer page) {
        Page<Goods> goodsPage = new Page<>(page,10);
        Page<Goods> resultPage = baseMapper.selectPage(goodsPage, new QueryWrapper<Goods>().orderByDesc("goods_id"));
        return getGoodsResult(resultPage);
    }

    @Override
    public Result getGoodsByName(String goodsName, Integer page) {
        Page<Goods> goodsPage = new Page<>(page,10);
        Page<Goods> resultPage = baseMapper.selectPage(goodsPage, new QueryWrapper<Goods>().like("goods_name",goodsName).orderByDesc("goods_id"));
        if (resultPage.getRecords().size() > 0){
            return getGoodsResult(resultPage);
        }
        return Result.success(null);
    }

    @Override
    public Result getGoodsByCategory(Integer category,Integer page) {
        Page<Goods> goodsPage = new Page<>(page,10);
        Page<Goods> resultPage = baseMapper.selectPage(goodsPage, new QueryWrapper<Goods>().eq("goods_category",category).orderByDesc("goods_id"));
        if (resultPage.getRecords().size() > 0){
            return getGoodsResult(resultPage);
        }
        return Result.success(null);
    }

}
