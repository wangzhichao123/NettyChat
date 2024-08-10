package com.wzc.netty.service;

import com.wzc.netty.pojo.dto.UserDetailsDTO;
import io.netty.channel.Channel;

public interface LoginStrategy {

    void login(Channel channel, String loginData, Integer loginType);
}
