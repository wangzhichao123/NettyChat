package com.wzc.netty.util;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.lang.generator.UUIDGenerator;
import cn.hutool.core.util.RandomUtil;

import java.util.Random;

public class RandomIDUtil {
    private static final int ID_LENGTH = 10;
    private static final Random RANDOM = new Random();
    private static final ThreadLocal<StringBuilder> SB_HOLDER = ThreadLocal.withInitial(StringBuilder::new);

    public static String generateRandomID() {
        StringBuilder sb = SB_HOLDER.get();
        sb.setLength(0);
        int firstDigit;
        do {
            firstDigit = RANDOM.nextInt(9) + 1;
        } while (firstDigit == 0);
        sb.append(firstDigit);

        for (int i = 1; i < ID_LENGTH; i++) {
            int digit = RANDOM.nextInt(10);
            sb.append(digit);
        }

        return sb.toString();
    }


    public static String generateRandomUUID() {
        return UUID.randomUUID().toString().replace("-","");
    }

    public static Long generateRandomLong() {
        return RandomUtil.randomLong();
    }
}
