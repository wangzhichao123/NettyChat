package com.wzc.netty.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Getter
@AllArgsConstructor
public enum CommandTypeEnum {

    LOGIN_MESSAGE(1, "登录消息"),

    SEND_MESSAGE(2, "发送消息"),

    ACK_MESSAGE(3, "确认消息"),

    HEARTBEAT_MESSAGE(4, "心跳消息");

    private final Integer cmd;
    private final String desc;

    private static Map<Integer, CommandTypeEnum> cache;

    static {
        cache = Arrays.stream(CommandTypeEnum.values()).collect(Collectors.toMap(CommandTypeEnum::getCmd, Function.identity()));
    }

    public static CommandTypeEnum of(Integer type) {
        return cache.get(type);
    }
}
