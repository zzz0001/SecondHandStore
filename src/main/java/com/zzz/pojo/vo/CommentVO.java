package com.zzz.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zzz
 * @date 2022/3/4 15:48
 */
@Data
public class CommentVO implements Serializable {

    @ApiModelProperty(value = "学号")
    private Long studentId;

    @ApiModelProperty(value = "商品ID")
    private Long goodsId;

    @ApiModelProperty(value = "订单Id")
    private Long orderId;

    @ApiModelProperty(value = "评价内容")
    private String content;

    @ApiModelProperty(value = "评价星级")
    private Integer grade;


    @ApiModelProperty(value = "商品图片链接")
    private List<String> images;

}
