package com.zzz.pojo.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author zzz
 * @date 2022/3/19 20:45
 */
@Data
public class OrderListVo implements Serializable {

    @ApiModelProperty(value = "订单号")
    private ArrayList<Long> orderIdList;

    @ApiModelProperty(value = "密码")
    private String password;

}
