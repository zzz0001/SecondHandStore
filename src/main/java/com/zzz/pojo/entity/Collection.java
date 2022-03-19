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
 * 收藏表
 * </p>
 *
 * @author zzz
 * @since 2022-03-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Collection对象", description="收藏表")
public class Collection implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商品收藏编号")
    @TableId(value = "collection_id", type = IdType.AUTO)
    private Long collectionId;

    @ApiModelProperty(value = "商品ID")
    private Long goodsId;

    @ApiModelProperty(value = "学号")
    private Long studentId;

    @ApiModelProperty(value = "商品类别")
    private Integer goodsCategory;

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
