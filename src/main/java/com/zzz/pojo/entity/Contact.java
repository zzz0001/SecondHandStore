package com.zzz.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author zzz
 * @since 2022-03-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Contact对象", description="聊天对象表")
public class Contact implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "联系人Id")
    @TableId(value = "contact_id", type = IdType.AUTO)
    private Long contactId;

    @ApiModelProperty(value = "学号")
    private Long studentId;

    @ApiModelProperty(value = "卖家学号")
    private Long sellerId;


}
