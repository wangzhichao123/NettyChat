package com.wzc.netty.context;

import com.wzc.netty.pojo.ChatRoom;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class WebSocketChannelContext {

    /**
     * key: 用户ID
     * value: 存储当前用户所有登录的channel
     */
    public static final ConcurrentHashMap<String, CopyOnWriteArrayList<Channel>> M_DEVICE_ONLINE_USER_MAP = new ConcurrentHashMap<>();


    public static final ChannelGroup GLOBAL_ONLINE_USER_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * key: 群组ID
     * value: 群聊信息
     */
    public static final ConcurrentHashMap<String, ChatRoom> CHAT_ROOMS = new ConcurrentHashMap<>();


    public static final ConcurrentHashMap<Channel, String> FIRST_CHANNEL_MAP = new ConcurrentHashMap<>();




}
