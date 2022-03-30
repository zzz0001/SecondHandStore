package com.zzz.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 花销表
 * </p>
 *
 * @author zzz
 * @since 2022-03-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Expense对象", description="花销表")
public class Expense implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "消费Id")
    @TableId(value = "expense_id", type = IdType.AUTO)
    private Long expenseId;

    @ApiModelProperty(value = "学号")
    private Long studentId;

    @ApiModelProperty(value = "收款人学号")
    private Long receiveId;

    @ApiModelProperty(value = "订单编号")
    private Long orderId;

    @ApiModelProperty(value = "状态（0 消费支出，1 提现，2 订单收入，3 充值）")
    private Integer status;

    @ApiModelProperty(value = "花费")
    private Double cost;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

}
