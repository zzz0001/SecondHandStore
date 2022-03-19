package com.zzz.controller;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.zzz.Util.Result;
import com.zzz.service.ImageService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zzz
 * @since 2022-03-03
 */
@RestController
@RequestMapping("/image")
public class ImageController {

    @Resource
    private ImageService imageService;

    @RequiresAuthentication
    @PostMapping("/upload")
    public Result uploadFile(@RequestParam("file") MultipartFile file) {
        String filePath = imageService.uploadFile(file);
        if (filePath == null) {
            return Result.fail("图片上传失败");
        }
        return Result.success(filePath);
    }

    @RequiresAuthentication
    @DeleteMapping("/remove")
    public Result removeFile(@RequestParam("path") String path) {
        if (!StringUtils.isNotBlank(path)) {
            return Result.fail("图片路径不能为空");
        }
        Boolean flag = imageService.removeFile(path);
        if (flag) {
            return Result.success("图片删除成功");
        }
        return Result.fail("图片不存在");
    }

    @RequiresAuthentication
    @DeleteMapping("/removeByGoodsId/{id}")
    public Result removeByGoodsId(@PathVariable Long id) {
        Result result = imageService.removeByGoodsId(id);
        return result;
    }

    @RequiresAuthentication
    @DeleteMapping("/removeByCommentId/{id}")
    public Result removeByCommentId(@PathVariable Long id) {
        Result result = imageService.removeByCommentId(id);
        return result;
    }
}

