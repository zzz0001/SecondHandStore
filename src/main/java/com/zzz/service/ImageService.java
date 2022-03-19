package com.zzz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzz.Util.Result;
import com.zzz.pojo.entity.Image;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zzz
 * @since 2022-03-03
 */
public interface ImageService extends IService<Image> {

    String uploadFile(MultipartFile file);

    Boolean removeFile(String path);

    Result removeByCommentId(Long id);

    Result removeByGoodsId(Long id);
}
