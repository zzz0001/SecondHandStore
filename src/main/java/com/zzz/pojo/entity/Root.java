package com.zzz.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 管理员表
 * </p>
 *
 * @author zzz
 * @since 2022-03-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Root对象", description="管理员表")
public class Root implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "管理员账号")
    @TableId(value = "student_id", type = IdType.AUTO)
    private Long studentId;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "权限")
    private String perms;

    @ApiModelProperty(value = "用户角色")
    private String roles;

}
