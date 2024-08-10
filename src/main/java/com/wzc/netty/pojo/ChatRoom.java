package com.wzc.netty.pojo;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ChatRoom {
    private ChannelGroup channelGroup;

    public ChatRoom() {
        this.channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    public ChannelGroup getChannelGroup() {
        return channelGroup;
    }
}
