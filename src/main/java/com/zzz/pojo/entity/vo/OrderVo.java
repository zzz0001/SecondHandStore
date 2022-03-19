package com.zzz.pojo.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zzz
 * @date 2022/3/19 20:45
 */
@Data
public class OrderVo implements Serializable {

    @ApiModelProperty(value = "订单号")
    private Long orderId;

    @ApiModelProperty(value = "密码")
    private String password;

}
