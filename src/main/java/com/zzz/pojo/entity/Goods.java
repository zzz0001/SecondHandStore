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
 * 商品表	
 * </p>
 *
 * @author zzz
 * @since 2022-03-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Goods对象", description="商品表	")
public class Goods implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商品ID")
    @TableId(value = "goods_id", type = IdType.AUTO)
    private Long goodsId;

    @ApiModelProperty(value = "学号")
    private Long studentId;

    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty(value = "商品介绍")
    private String goodsIntroduce;

    @ApiModelProperty(value = "商品类别")
    private Integer goodsCategory;

    @ApiModelProperty(value = "商品价格")
    private Double goodsPrice;

    @ApiModelProperty(value = "商品库存")
    private Integer goodsInventory;

    @ApiModelProperty(value = "版本号")
    @Version
    private Integer version;

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
