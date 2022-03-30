package com.zzz.service;

import com.zzz.Util.Result;
import com.zzz.pojo.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zzz.pojo.entity.vo.CommentVO;

/**
 * <p>
 * 评价 服务类
 * </p>
 *
 * @author zzz
 * @since 2022-03-02
 */
public interface CommentService extends IService<Comment> {

    Result saveComment(CommentVO commentVO);

    Result removeComment(Long commentId);

    Result getByGoodsId(Long goodsId, Integer page);
}
