package com.zzz.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 账户
 * </p>
 *
 * @author zzz
 * @since 2022-03-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Account对象", description="账户")
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "学号")
    @TableId(value = "student_id", type = IdType.INPUT)
    private Long studentId;

    @ApiModelProperty(value = "账户余额")
    private Double money;

    @ApiModelProperty(value = "信用等级")
    private Integer credit;

    @ApiModelProperty(value = "支付密码")
    private String password;

    @ApiModelProperty(value = "账户状态(0正常,1被锁定)")
    private Integer status;

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
