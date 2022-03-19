package com.zzz.pojo.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @author zzz
 * @date 2022/3/3 22:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class GoodsVO implements Serializable {

    @ApiModelProperty(value = "商品ID")
    private Long goodsId;

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

    @ApiModelProperty(value = "商品图片链接")
    private List<String> images;

}
