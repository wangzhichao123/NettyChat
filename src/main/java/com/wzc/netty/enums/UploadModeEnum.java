package com.wzc.netty.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum UploadModeEnum {

    OSS(1, "ossUploadStrategyImpl"),

    QNY(2, "qnyUploadStrategyImpl"),

    MINIO(3, "minioUploadStrategyImpl");

    private final Integer mode;

    private final String strategy;

    private static Map<Integer, UploadModeEnum> cache;

    static {
        cache = Arrays.stream(UploadModeEnum.values()).collect(Collectors.toMap(UploadModeEnum::getMode, Function.identity()));
    }

    public static String of(Integer type) {
        return cache.get(type).getStrategy();
    }
}
