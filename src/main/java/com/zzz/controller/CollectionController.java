package com.zzz.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzz.Util.JwtUtils;
import com.zzz.Util.Result;
import com.zzz.pojo.entity.Collection;
import com.zzz.service.CollectionService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 收藏表 前端控制器
 * </p>
 *
 * @author zzz
 * @since 2022-03-02
 */
@RestController
public class CollectionController {

    @Resource
    private CollectionService collectionService;

    @Resource
    private JwtUtils jwtUtils;

    @RequiresAuthentication
    @PostMapping("/collection")
    public Result add(@RequestBody Collection collection) {
        collection.setDeleted(false);
        boolean save = collectionService.save(collection);
        if (save) {
            return Result.success("收藏成功");
        }
        return Result.fail("收藏失败");
    }

    @RequiresAuthentication
    @DeleteMapping("/collection/{collectionId}")
    public Result remove(@PathVariable Long collectionId) {
        boolean remove = collectionService.removeById(collectionId);
        if (remove) {
            return Result.success("取消收藏成功");
        }
        return Result.fail("取消收藏失败");
    }

    @RequiresAuthentication
    @GetMapping("/collection/{collectionID}")
    public Result get(@PathVariable Long collectionID) {
        Collection collection = collectionService.getById(collectionID);
        if (collection == null) {
            return Result.fail("找不到该收藏ID");
        }
        return Result.success(collection);
    }

    @RequiresAuthentication
    @GetMapping("/collection/{pageId}")
    public Result page(@PathVariable Integer pageId,
                       HttpServletRequest request) {
        Page<Collection> page = new Page<>(pageId, 10);
        Long studentId = jwtUtils.getStudentId(request);
        QueryWrapper<Collection> queryWrapper = new QueryWrapper<Collection>().eq("student_id", studentId).orderByDesc("collection_id");
        Page<Collection> collectionPage = collectionService.page(page, queryWrapper);
        return Result.success(collectionPage);
    }

}

