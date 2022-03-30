package com.zzz.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author zzz
 * @since 2022-03-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="User对象", description="用户表")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "学号")
    @TableId(value = "student_id", type = IdType.INPUT)
    private Long studentId;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "头像链接")
    private String picture;

    @ApiModelProperty(value = "真实姓名")
    private String realName;

    @TableField(exist = false)
    @ApiModelProperty(value = "年龄")
    private Integer age;

    @JsonFormat(pattern = "yyyy-MM-dd",timezone="Asia/Shanghai")
    @ApiModelProperty(value = "出生年月")
    private Date birthDay;

    @ApiModelProperty(value = "电话")
    private String phone;

    @ApiModelProperty(value = "性别（0女1男）")
    private Integer sex;

    @ApiModelProperty(value = "地址")
    private String address;

    @ApiModelProperty(value = "个人信息")
    private String info;

    @ApiModelProperty(value = "权限")
    private String perms;

    @ApiModelProperty(value = "用户角色")
    private String roles;

    @ApiModelProperty(value = "账号状态（0正常,1被锁定,2开通账户,3开通店铺）")
    private Integer status;

    @ApiModelProperty(value = "账号锁定前的账号状态")
    private Integer lastStatus;

    @ApiModelProperty(value = "逻辑删除")
    @TableLogic
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;


}
