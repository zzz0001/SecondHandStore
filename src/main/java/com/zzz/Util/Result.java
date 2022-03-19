package com.zzz.Util;

import com.zzz.exception.ExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zzz
 * @date 2022/2/28 18:30
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {

    // 状态码：200表示成功，其他表示失败
    private Integer code;
    // 操作结果信息
    private String message;
    // 返回的结果
    private T data;

    public static <T> Result<T> success(T data){
        return success(200,"操作成功",data);
    }

    public static <T> Result<T> success(Integer code,String message){
        return success(code,message,null);
    }

    public static <T> Result<T> success(String message,T data){
        return success(200,message,data);
    }

    public static <T> Result<T> success(Integer code,String message,T data){
        return new Result<T>(code,message,data);
    }

    public static <T> Result<T> fail(Integer code){
        return fail(code,"操作失败",null);
    }

    public static <T> Result<T> fail(String message){
        return fail(400,message,null);
    }

    public static <T> Result<T> fail(Integer code,String message){
        return fail(code,message,null);
    }

    public static <T> Result<T> fail(ExceptionEnum exceptionEnum){
        return fail(exceptionEnum.getCode(),exceptionEnum.getMessage(),null);
    }

    public static <T> Result<T> fail(Integer code,String message,T data){
        return new Result<T>(code,message,data);
    }

}