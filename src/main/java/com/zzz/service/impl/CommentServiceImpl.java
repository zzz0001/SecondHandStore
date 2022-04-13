package com.zzz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzz.Util.Result;
import com.zzz.exception.BusinessException;
import com.zzz.mapper.CommentMapper;
import com.zzz.mapper.OrdersMapper;
import com.zzz.mapper.UserMapper;
import com.zzz.pojo.entity.Comment;
import com.zzz.pojo.entity.Image;
import com.zzz.pojo.entity.Orders;
import com.zzz.pojo.entity.User;
import com.zzz.pojo.entity.vo.CommentVO;
import com.zzz.service.CommentService;
import com.zzz.service.ImageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 评价 服务实现类
 * </p>
 *
 * @author zzz
 * @since 2022-03-02
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Resource
    private ImageService imageService;

    @Resource
    private OrdersMapper ordersMapper;

    @Resource
    private UserMapper userMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result saveComment(CommentVO commentVO) {
        Comment comment = new Comment();
        BeanUtil.copyProperties(commentVO,comment);
        comment.setDeleted(false);
        int insert = baseMapper.insert(comment);
        Long commentId = comment.getCommentId();
        List<String> imagesPath = commentVO.getImages();
        ArrayList<Image> images = new ArrayList<>();
        imagesPath.forEach(path -> {
            Image image = new Image();
            image.setCommentId(commentId);
            image.setImagePath(path);
            images.add(image);
        });
        boolean sava = imageService.saveBatch(images);
        if (insert == 1 && sava){
            Long orderId = commentVO.getOrderId();
            Orders orders = ordersMapper.selectById(orderId);
            orders.setOrderStatus(4);
            ordersMapper.updateById(orders);
            return Result.success("评价成功");
        }
        return Result.fail("评价失败");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result removeComment(Long commentId) {
        int b = baseMapper.deleteById(commentId);
        imageService.removeByCommentId(commentId);
        if (b==1) {
            return Result.success("评论删除成功");
        }
        throw new BusinessException("评论删除失败");

    }

    @Override
    public Result getByGoodsId(Long goodsId, Integer page) {
        Page<Comment> commentPage = new Page<>(page, 10);
        QueryWrapper<Comment> wrapper = new QueryWrapper<Comment>().eq("goods_id",goodsId).orderByDesc("comment_id");
        Page<Comment> comments = baseMapper.selectPage(commentPage, wrapper);
        List<Comment> commentList = comments.getRecords();
        ArrayList<Object> resultComment = new ArrayList<>();
        commentList.forEach(comment -> {
            HashMap<String, Object> map = new HashMap<>();
            Long studentId = comment.getStudentId();
            User user = userMapper.selectById(studentId);
            List<String> images = imageService.getImagesByCommentId(comment.getCommentId());
            map.put("user",user);
            map.put("comment",comment);
            map.put("images",images);
            resultComment.add(map);
        });
        comments.setRecords(null);
        HashMap<String, Object> result = new HashMap<>();
        result.put("comment",resultComment);
        result.put("page",comments);
        return Result.success(result);
    }

    @Override
    public void removeCommentsByGoodsId(Long goodsId) {
        QueryWrapper<Comment> wrapper = new QueryWrapper<Comment>().eq("goods_id", goodsId);
        List<Comment> comments = baseMapper.selectList(wrapper);
        comments.forEach( comment -> {
            imageService.removeByCommentId(comment.getCommentId());
            baseMapper.deleteById(comment.getCommentId());
        });
    }
}
