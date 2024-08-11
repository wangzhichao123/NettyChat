package com.wzc.netty.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusCodeEnum {

    SUCCESS(200, "成功"),   // 成功

    FAIL(400, "失败"),      // 失败

    NO_LOGIN(401, "用户未登录"), // 未认证

    AUTHORIZED(403, "没有操作权限"), // 无权限

    SYSTEM_ERROR(500, "系统异常"),   // 服务器内部错误

    VALID_ERROR(422, "参数校验失败"), // 参数非法

    LOGIN_SUCCESS(10001, "登录成功"),

    LOGIN_ERROR(10002, "登录错误"),

    MESSAGE_SEND_SUCCESS(10003, "私聊/群组消息发送成功"),

    MESSAGE_SEND_ERROR(10004, "私聊/群组消息发送失败"),

    INVALID_TOKEN(10005, "Token解析失败"),

    INVALID_MESSAGE_TYPE(10006, "非法发送消息类型"),

    USER_NOT_EXIST(10007, "用户不存在"),

    USER_EXIST(10008, "用户已存在"),

    MESSAGE_ACK_ERROR(10009, "消息确认失败"),

    INVALID_ACK_MESSAGE(10010, "非法确认消息类型"),

    NOT_FRIENDS(10011, "非好友关系");

    private final Integer code;

    private final String desc;

}
