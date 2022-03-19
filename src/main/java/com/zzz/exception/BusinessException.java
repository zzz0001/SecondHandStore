package com.zzz.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zzz
 * @date 2022/2/28 19:34
 */

@Data
@NoArgsConstructor
public class BusinessException extends RuntimeException{

    //错误码
    private Integer code;
    //错误消息
    private String message;

    /**
     * @param message 错误消息
     */
    public BusinessException(String message) {
        this.message = message;
    }

    /**
     * @param message 错误消息
     * @param code 错误码
     */
    public BusinessException(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    /**
     * @param message 错误消息
     * @param code 错误码
     * @param cause 原始异常对象
     */
    public BusinessException(String message, Integer code, Throwable cause) {
        super(cause);
        this.message = message;
        this.code = code;
    }

    /**
     * @param exceptionEnum 异常枚举类
     */
    public BusinessException(ExceptionEnum exceptionEnum) {
        this.message = exceptionEnum.getMessage();
        this.code = exceptionEnum.getCode();
    }

    /**
     * 将抛出的异常也捕获到，放到全局异常处理类中进行处理
     * @param exceptionEnum 接收枚举类型
     * @param cause 原始异常对象
     */
    public BusinessException(ExceptionEnum exceptionEnum, Throwable cause) {
        super(cause);
        this.message = exceptionEnum.getMessage();
        this.code = exceptionEnum.getCode();
    }
}