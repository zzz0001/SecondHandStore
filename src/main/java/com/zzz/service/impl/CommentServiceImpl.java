package com.zzz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.zzz.Util.Result;
import com.zzz.pojo.entity.Comment;
import com.zzz.mapper.CommentMapper;
import com.zzz.pojo.entity.Image;
import com.zzz.pojo.entity.vo.CommentVO;
import com.zzz.service.CommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzz.service.ImageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
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
        return Result.success("评论删除失败");

    }
}