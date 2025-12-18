package com.example.msBackend.pojo.Vo;

import lombok.Data;

@Data
public class ResultVo<T> {
    private Integer code; //编码：1成功，0和其它数字为失败
    private String msg = " "; //错误信息
    private T data; //数据

    public static <T> ResultVo<T> success() {
        ResultVo<T> result = new ResultVo<T>();
        result.code = 1;
        return result;
    }

    public static <T> ResultVo<T> success(T object) {
        ResultVo<T> result = new ResultVo<T>();
        result.data = object;
        result.code = 200;
        return result;
    }

    public static <T> ResultVo<T> success(String message, T object) {
        ResultVo<T> result = new ResultVo<T>();
        result.msg = message;
        result.data = object;
        result.code = 200;
        return result;
    }

    public static <T> ResultVo<T> error(String msg) {
        ResultVo<T> result = new ResultVo<T>();
        result.msg = msg;
        result.code = 201;
        return result;
    }


}
