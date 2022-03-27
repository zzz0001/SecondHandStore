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
 * 聊天表
 * </p>
 *
 * @author zzz
 * @since 2022-03-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Chat对象", description="聊天表")
public class Chat implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "聊天记录ID")
    @TableId(value = "chat_id", type = IdType.AUTO)
    private Long chatId;

    @ApiModelProperty(value = "消息发送者")
    private Long sendId;

    @ApiModelProperty(value = "消息接受者")
    private Long receiveId;

    @ApiModelProperty(value = "消息内容")
    private String message;

    @ApiModelProperty(value = "是否已读(0是已读，1是未读)")
    private Integer isRead;

    @ApiModelProperty(value = "逻辑删除")
    @TableLogic
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

}
