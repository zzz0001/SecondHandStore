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
 * 店铺表
 * </p>
 *
 * @author zzz
 * @since 2022-03-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Store对象", description="店铺表")
public class Store implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商家编号")
    @TableId(value = "store_id", type = IdType.AUTO)
    private Long storeId;

    @ApiModelProperty(value = "学号")
    private Long studentId;

    @ApiModelProperty(value = "店铺名称")
    private String storeName;

    @ApiModelProperty(value = "店铺评分")
    private Float storeEvaluation;

    @ApiModelProperty(value = "店铺描述")
    private String storeIntroduce;

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
