package com.zzz.shiro;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zzz
 * @date 2022/3/1 14:50
 */

@Data
public class UserProfile implements Serializable {

    private Long studentId;

    private String userName;

}
