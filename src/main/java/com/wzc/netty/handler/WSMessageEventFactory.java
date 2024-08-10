package com.wzc.netty.handler;

import com.lmax.disruptor.EventFactory;
import com.wzc.netty.pojo.dto.MessageModel;

public class WSMessageEventFactory implements EventFactory<MessageModel> {
    @Override
    public MessageModel newInstance() {
        return new MessageModel();
    }
}
