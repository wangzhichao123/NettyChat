package com.wzc.netty.pojo;

import com.wzc.netty.enums.StatusCodeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class R<T> {
    /**
     * 消息状态标识
     */
    private Boolean status;
    /**
     * 状态码
     */
    private Integer code;
    /**
     * 消息内容
     */
    private String msg;
    /**
     * 数据
     */
    private T data;

    public static <T> R<T> ok() {
        return R.<T>builder()
                .status(true)
                .code(StatusCodeEnum.SUCCESS.getCode())
                .msg(StatusCodeEnum.SUCCESS.getDesc())
                .build();
    }

    public static <T> R<T> ok(T data) {
        return R.<T>builder()
                .status(true)
                .code(StatusCodeEnum.SUCCESS.getCode())
                .msg(StatusCodeEnum.SUCCESS.getDesc())
                .data(data)
                .build();
    }

    public static <T> R<T> ok(T data, String msg){
        return R.<T>builder()
                .status(true)
                .code(StatusCodeEnum.SUCCESS.getCode())
                .msg(msg)
                .data(data)
                .build();
    }

    public static <T> R<T> ok(T data, StatusCodeEnum statusCodeEnum) {
        return R.<T>builder()
                .code(statusCodeEnum.getCode())
                .msg(statusCodeEnum.getDesc())
                .status(true)
                .data(data)
                .build();
    }

    public static <T> R<T> fail() {
        return R.<T>builder()
                .status(false)
                .code(StatusCodeEnum.FAIL.getCode())
                .msg(StatusCodeEnum.FAIL.getDesc())
                .build();
    }

    public static <T> R<T> fail(T data) {
        return R.<T>builder()
                .status(false)
                .code(StatusCodeEnum.FAIL.getCode())
                .msg(StatusCodeEnum.FAIL.getDesc())
                .data(data)
                .build();
    }

    public static <T> R<T> fail(String msg) {
        return R.<T>builder()
                .status(false)
                .code(StatusCodeEnum.FAIL.getCode())
                .msg(msg)
                .build();
    }

    public static <T> R<T> fail(String msg, StatusCodeEnum statusCodeEnum) {
        return R.<T>builder()
                .status(false)
                .code(statusCodeEnum.getCode())
                .msg(msg)
                .build();
    }

    public static <T> R<T> fail(StatusCodeEnum statusCodeEnum) {
        return R.<T>builder()
                .status(false)
                .code(statusCodeEnum.getCode())
                .msg(statusCodeEnum.getDesc())
                .build();
    }

    public static <T> R<T> fail(StatusCodeEnum statusCodeEnum, T data) {
        return R.<T>builder()
                .status(false)
                .code(statusCodeEnum.getCode())
                .msg(statusCodeEnum.getDesc())
                .data(data)
                .build();
    }

    public static <T> R<T> fail(Integer code, String msg) {
        return R.<T>builder()
                .status(false)
                .code(code)
                .msg(msg)
                .build();
    }
}
