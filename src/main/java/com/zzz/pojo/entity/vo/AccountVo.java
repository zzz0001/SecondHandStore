package com.zzz.pojo.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zzz
 * @date 2022/3/16 20:56
 */
@Data
public class AccountVo implements Serializable {

    private Double money;

    private String password;

}
