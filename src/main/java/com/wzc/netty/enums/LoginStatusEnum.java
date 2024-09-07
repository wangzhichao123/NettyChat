package com.wzc.netty.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
@Getter
@AllArgsConstructor
public enum LoginStatusEnum {

    ONLINE_STATUS(1, "在线"),

    OFFLINE_STATUS(0, "离线");

    private final Integer code;

    private final String desc;

    private static Map<Integer, LoginStatusEnum> cache;

    static {
        cache = Arrays.stream(LoginStatusEnum.values()).collect(Collectors.toMap(LoginStatusEnum::getCode, Function.identity()));
    }

    public static LoginStatusEnum of(Integer type) {
        return cache.get(type);
    }
}
