package com.wzc.netty.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum SendMessageTypeEnum {

    TEXT(1, "文本消息和表情包消息"),

    IMAGE(2, "图片消息"),

    FILE(3, "文件消息"),

    AUDIO(4, "语音消息"),

    VIDEO(5, "视频消息");
    private final Integer code;

    private final String desc;

    private static Map<Integer, SendMessageTypeEnum> cache;

    static {
        cache = Arrays.stream(SendMessageTypeEnum.values()).collect(Collectors.toMap(SendMessageTypeEnum::getCode, Function.identity()));
    }

    public static SendMessageTypeEnum of(Integer type) {
        return cache.get(type);
    }
}
