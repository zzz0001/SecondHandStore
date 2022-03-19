package com.zzz.pojo.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zzz
 * @date 2022/3/1 18:41
 */
@Data
public class UserVo implements Serializable {

    @ApiModelProperty(value = "学号")
    private Long studentId;

    @ApiModelProperty(value = "密码")
    private String password;

}
