package com.wzc.netty.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum MessageStatusEnum {
    MESSAGE_INIT(1, "消息初始化"),

    MESSAGE_SENDING(2, "消息发送中"),

    MESSAGE_SUCCESS_SEND(3, "消息发送成功"),

    MESSAGE_ERROR_SEND(4, "消息发送失败"),

    MESSAGE_REVOKED(5, "消息撤回"),

    MESSAGE_OFFLINE(6, "离线消息"),;

    private final Integer code;

    private final String desc;

    private static Map<Integer, MessageStatusEnum> cache;

    static {
        cache = Arrays.stream(MessageStatusEnum.values()).collect(Collectors.toMap(MessageStatusEnum::getCode, Function.identity()));
    }

    public static MessageStatusEnum of(Integer type) {
        return cache.get(type);
    }
}
