package com.wzc.netty.service;

import com.wzc.netty.pojo.dto.LoginDTO;
import com.wzc.netty.pojo.dto.UserDetailsDTO;
import io.netty.channel.Channel;

public interface LoginService {

    void login(Channel channel, LoginDTO loginDTO);

    void tokenLogin(Channel channel, String token);
}