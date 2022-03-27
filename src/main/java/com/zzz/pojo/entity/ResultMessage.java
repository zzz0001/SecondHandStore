package com.zzz.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zzz
 * @date 2022/3/23 19:15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResultMessage {

//    是否是系统消息
    private boolean systemMsgFlag;

//    消息类别
    private Integer messageCode;

//     发送方Name
    private String fromName;

//     发送的数据
    private Object message;

}
