package com.wzc.netty.context;

import com.wzc.netty.enums.LoginTypeEnum;
import com.wzc.netty.service.LoginStrategy;
import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component
public class LoginStrategyContext {
    @Resource
    private Map<String, LoginStrategy> loginStrategyMap;

    public void executeLoginStrategy(Channel channel, String loginData, LoginTypeEnum loginTypeEnum) {
        loginStrategyMap.get(loginTypeEnum.getStrategy()).login(channel, loginData, loginTypeEnum.getType());
    }

}


