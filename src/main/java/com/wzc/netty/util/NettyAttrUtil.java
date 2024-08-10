package com.wzc.netty.util;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;


public class NettyAttrUtil {

    public static final AttributeKey<String> ATTR_KEY_USER_ID = AttributeKey.valueOf("userId");

    public static final AttributeKey<String> ATTR_KEY_TOKEN = AttributeKey.valueOf("token");

    public static final AttributeKey<String> ATTR_KEY_IP = AttributeKey.valueOf("ip");

    public static final AttributeKey<String> LAST_ACTIVE_TIME_ATTR = AttributeKey.valueOf("lastActiveTime");


    public static void setAttrKeyUserId(Channel channel, String userId) {
        channel.attr(ATTR_KEY_USER_ID).set(userId);
    }

    public static String getAttrKeyUserId(Channel channel) {
        return getAttribute(channel, ATTR_KEY_USER_ID);
    }

    public static void setAttrKeyLastActiveTime(Channel channel, String time) {
        channel.attr(LAST_ACTIVE_TIME_ATTR).set(time);
    }

    public static String getAttrKeyLastActiveTime(Channel channel) {
        return getAttribute(channel, LAST_ACTIVE_TIME_ATTR);
    }
    public static void setAttrKeyToken(Channel channel, String token) {
        channel.attr(ATTR_KEY_TOKEN).set(token);
    }

    public static String getAttrKeyToken(Channel channel) {
        return getAttribute(channel, ATTR_KEY_TOKEN);
    }

    public static void setAttrKeyIP(Channel channel, String ip) {
        channel.attr(ATTR_KEY_IP).set(ip);
    }

    public static String getAttrKeyIP(Channel channel) {
        return getAttribute(channel, ATTR_KEY_IP);
    }

    private static String getAttribute(Channel channel, AttributeKey<String> key) {
        return channel.attr(key).get();
    }

}
