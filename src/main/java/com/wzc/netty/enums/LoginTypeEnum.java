package com.wzc.netty.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum LoginTypeEnum {

    ACCOUNT(1, "账号登录", "accountLoginStrategyImpl"),

    EMAIL(2, "邮箱登录", "emailLoginStrategyImpl"),

    PHONE(3, "手机号登录", "phoneLoginStrategyImpl"),

    QQ(4, "QQ登录", "qqLoginStrategyImpl"),

    WX(5, "微信登录", "wxLoginStrategyImpl"),

    GITHUB(6, "GitHub登录", "githubLoginStrategyImpl"),

    GITEE(7, "Gitee登录", "giteeLoginStrategyImpl");

    private final Integer type;

    private final String desc;

    private final String strategy;

    private static Map<Integer, LoginTypeEnum> cache;

    static {
        cache = Arrays.stream(LoginTypeEnum.values()).collect(Collectors.toMap(LoginTypeEnum::getType, Function.identity()));
    }

    public static LoginTypeEnum of(Integer type) {
        return cache.get(type);
    }

}
