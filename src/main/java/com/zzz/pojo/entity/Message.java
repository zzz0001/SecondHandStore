package com.zzz.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zzz
 * @date 2022/3/23 19:23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message {
    // 接收方
    private String toName;

    // 发送的数据
    private String message;
}