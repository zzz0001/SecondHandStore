package com.zzz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzz.Util.Result;
import com.zzz.mapper.ImageMapper;
import com.zzz.pojo.entity.Image;
import com.zzz.service.ImageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zzz
 * @since 2022-03-03
 */
@Service
public class ImageServiceImpl extends ServiceImpl<ImageMapper, Image> implements ImageService {

    @Override
    public String uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            return null;
        }
        String originalFilename = file.getOriginalFilename();
        String FileName = System.currentTimeMillis() + originalFilename.substring(originalFilename.lastIndexOf("."));
        String FilePath = "D:/IDEA workspace/secondhandstorevue/public/user/" + FileName;
        File DestFile = new File(FilePath);
        if (!DestFile.getParentFile().exists()) {
            boolean res = DestFile.getParentFile().mkdirs();
        }
        try {
            file.transferTo(DestFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return "/user/" + FileName;
    }

    @Override
    public Boolean removeFile(String path) {
        String newPath = "D:/IDEA workspace/secondhandstorevue/public" + path;
        boolean delete = new File(newPath).delete();
        QueryWrapper<Image> wrapper = new QueryWrapper<Image>().eq("image_path", path);
        int i = baseMapper.delete(wrapper);
        return delete;
    }

    @Override
    public Result removeByCommentId(Long id) {
        int delete = baseMapper.delete(new QueryWrapper<Image>().eq("comment_id", id));
        if (delete > 0) {
            return Result.success("删除成功");
        }
        return Result.fail("删除失败");
    }

    @Override
    public Result removeByGoodsId(Long id) {
        int delete = baseMapper.delete(new QueryWrapper<Image>().eq("goods_id", id));
        if (delete >= 0) {
            return Result.success("删除成功");
        }
        return Result.fail("删除失败");
    }

    @Override
    public List<String> getImagesByGoodsId(Long goodsId) {
        QueryWrapper<Image> wrapper = new QueryWrapper<Image>().eq("goods_id", goodsId);
        List<Image> images = baseMapper.selectList(wrapper);
        ArrayList<String> imageList = new ArrayList<>();
        images.forEach( image -> {
            imageList.add(image.getImagePath());
        });
        return imageList;
    }

    @Override
    public List<String> getImagesByCommentId(Long commentId) {
        QueryWrapper<Image> wrapper = new QueryWrapper<Image>().eq("comment_id", commentId);
        List<Image> images = baseMapper.selectList(wrapper);
        ArrayList<String> imageList = new ArrayList<>();
        images.forEach( image -> {
            imageList.add(image.getImagePath());
        });
        return imageList;
    }

}
