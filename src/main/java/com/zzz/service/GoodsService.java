package com.zzz.service;

import com.zzz.Util.Result;
import com.zzz.pojo.entity.Goods;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zzz.pojo.entity.vo.GoodsVO;

/**
 * <p>
 * 商品表	 服务类
 * </p>
 *
 * @author zzz
 * @since 2022-03-02
 */
public interface GoodsService extends IService<Goods> {

    Boolean saveOrUpdateGoodsVO(GoodsVO goodsVO, Long studentId);

    Result getGoods(Long goodsId);

    Result removeGoods(Long goodsId);

    Result addInventory(Long goodsId, Integer inventory);

    Result getGoodsByStudentId(Long studentId);
}
