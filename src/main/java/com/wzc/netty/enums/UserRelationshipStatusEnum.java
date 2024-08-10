package com.wzc.netty.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRelationshipStatusEnum {

    PENDING(1, "待验证"),

    APPROVED(2, "已验证"),

    REJECTED(3, "已拒绝");

    private final Integer code;

    private final String desc;
}
