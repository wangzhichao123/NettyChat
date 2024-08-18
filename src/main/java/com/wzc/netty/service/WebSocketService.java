package com.wzc.netty.service;

import com.wzc.netty.pojo.dto.WsReqDTO;
import io.netty.channel.Channel;

public interface WebSocketService {

    void addChannel(Channel channel);

    void handleLoginReq(Channel channel, String data, String token);

    void handleSendMessage(Channel channel, String data, String token);

    void tokenLogin(Channel channel, String token);

    void handleHeartbeat(Channel channel);

    void clearSession(Channel channel);

    void handleACKMessage(Channel channel, String data, String token);
}
