package com.zzz.pojo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zzz
 * @date 2022/4/1 16:12
 */
@Data
public class PasswordVo implements Serializable {

    private Long studentId;

    private String oldPassword;

    private String password;

}
