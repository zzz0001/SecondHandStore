package com.zzz.exception;

/**
 * @author zzz
 * @date 2022/2/28 19:36
 */
public enum ExceptionEnum {

    SUCCESS(200,"成功"),
    FAIL(100,"操作失败"),
    NAME_NOT_EXIST(101,"学号不存在"),
    NAME_EXIST(102,"学号已注册"),
    PARAM_EXCEPTION(103,"参数错误"),
    PARAM_NOT_MATCH(104,"参数类型不匹配"),
    REGISTER_ERROR(105,"注册失败,请稍后重试"),
    PASSWORD_ERROR(106,"密码错误"),
    PAGE_ERROR(404,"页面不存在"),
    NETWORK_ERROR(405,"网络错误");

    private Integer code;
    private String message;

    ExceptionEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
