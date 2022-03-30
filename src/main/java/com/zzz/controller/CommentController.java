package com.zzz.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zzz.Util.Result;
import com.zzz.pojo.entity.Comment;
import com.zzz.pojo.entity.Image;
import com.zzz.pojo.entity.vo.CommentVO;
import com.zzz.service.CommentService;
import com.zzz.service.ImageService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 评价 前端控制器
 * </p>
 *
 * @author zzz
 * @since 2022-03-02
 */
@RestController
public class CommentController {

    @Resource
    private CommentService commentService;

    @Resource
    private ImageService imageService;

    @RequiresAuthentication
    @GetMapping("/comment/{commentId}")
    public Result get(@PathVariable Long commentId) {
        Comment comment = commentService.getById(commentId);
        if (comment == null) {
            return Result.fail("找不到该商品评论");
        }
        return Result.success(comment);
    }

    @RequiresAuthentication
    @GetMapping("/comment/page/{goodsId}/{page}")
    public Result page(@PathVariable Long goodsId,
                       @PathVariable Integer page) {
        Result result = commentService.getByGoodsId(goodsId,page);
        return result;
    }

    @RequiresAuthentication
    @PostMapping("/comment")
    public Result comment(@RequestBody CommentVO commentVO){
        Result result = commentService.saveComment(commentVO);
        return result;
    }

    @RequiresAuthentication
    @DeleteMapping("/comment/{commentId}")
    public Result remove(@PathVariable Long commentId){
        Result result = commentService.removeComment(commentId);
        return result;
    }

    @RequiresAuthentication
    @DeleteMapping("/comment/removeImage/{commentId}")
    public Result removeImage(@PathVariable Long commentId){
        boolean remove = imageService.remove(new QueryWrapper<Image>().eq("comment_id", commentId));
        if (remove) {
            return Result.success("评价照片删除成功");
        }
        return Result.fail("评价照片删除失败");
    }
}

