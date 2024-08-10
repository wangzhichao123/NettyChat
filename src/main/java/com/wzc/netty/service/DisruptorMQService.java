package com.wzc.netty.service;

import com.wzc.netty.pojo.R;
import io.netty.channel.Channel;

public interface DisruptorMQService {

    void sendMsg(Channel channel, R<?> msg) ;

}
