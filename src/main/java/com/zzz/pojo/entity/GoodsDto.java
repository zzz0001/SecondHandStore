package com.zzz.pojo.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zzz
 * @date 2022/3/31 16:52
 */
@Data
public class GoodsDto implements Serializable {

    private Long goodsId;

    private Integer goodsNum;

}
