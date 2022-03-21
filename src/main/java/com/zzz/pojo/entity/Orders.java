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
 * 订单表
 * </p>
 *
 * @author zzz
 * @since 2022-03-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Orders对象", description="订单表")
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单编号")
    @TableId(value = "order_id", type = IdType.AUTO)
    private Long orderId;

    @ApiModelProperty(value = "商家编号")
    private Long storeId;

    @ApiModelProperty(value = "买家学号")
    private Long studentId;

    @ApiModelProperty(value = "商品编号")
    private Long goodsId;

    @ApiModelProperty(value = "商品数量")
    private Integer goodsNum;

    @ApiModelProperty(value = "商品总价")
    private Double totalPrice;

    @ApiModelProperty(value = "付款时间")
    private LocalDateTime orderDate;

    @ApiModelProperty(value = "发货时间")
    private LocalDateTime deliveryDate;

    @ApiModelProperty(value = "收货时间")
    private LocalDateTime receiveDate;

    @ApiModelProperty(value = "退货时间")
    private LocalDateTime returnDate;

    @ApiModelProperty(value = "订单状态（1确认付款，2确认发货，3确认收货，4待评价，5退货）")
    private Integer orderStatus;

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
