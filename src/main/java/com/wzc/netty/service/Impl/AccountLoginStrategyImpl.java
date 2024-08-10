package com.wzc.netty.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.wzc.netty.mapper.UserMapper;
import com.wzc.netty.pojo.R;
import com.wzc.netty.pojo.dto.AccountDTO;
import com.wzc.netty.pojo.dto.UserDetailsDTO;
import com.wzc.netty.pojo.entity.User;
import com.wzc.netty.service.DisruptorMQService;
import com.wzc.netty.service.LoginStrategy;
import com.wzc.netty.service.RedisService;
import com.wzc.netty.service.TokenService;
import com.wzc.netty.util.NettyAttrUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.wzc.netty.constant.RedisConstant.*;
import static com.wzc.netty.context.WebSocketChannelContext.*;
import static com.wzc.netty.enums.LoginStatusEnum.*;
import static com.wzc.netty.enums.StatusCodeEnum.*;


@Slf4j
@Service
public class AccountLoginStrategyImpl implements LoginStrategy {

    @Resource
    private UserMapper userMapper;

    @Resource
    private TokenService tokenService;

    @Resource
    private RedisService redisService;

    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Resource
    private DisruptorMQService disruptorMQService;
    @Override
    public void login(Channel channel, String loginData, Integer loginType) {
        UserDetailsDTO userDetailsDTO;
        AccountDTO accountDTO = JSONObject.parseObject(loginData, AccountDTO.class);
        if(StrUtil.isBlank(accountDTO.getUserId()) || StrUtil.isBlank(accountDTO.getPassword())){
            disruptorMQService.sendMsg(channel, R.fail("账号或密码不能为空", LOGIN_ERROR));
            return;
        }
        LambdaQueryChainWrapper<User> query = new LambdaQueryChainWrapper<>(userMapper);
        User user = query.eq(User::getUserId, accountDTO.getUserId()).one();
        if(ObjectUtil.isNull(user) || !bCryptPasswordEncoder.matches(accountDTO.getPassword(), user.getPassword())){
            disruptorMQService.sendMsg(channel, R.fail("账号或密码不正确", LOGIN_ERROR));
            return;
        }
        // 更新用户登录状态
        user.setStatus(ONLINE_STATUS.getCode());
        user.setOnlineTime(LocalDateTime.now());
        userMapper.updateById(user);

        if(FIRST_CHANNEL_MAP.containsKey(channel)){
            // 放入用户ID
            FIRST_CHANNEL_MAP.put(channel, accountDTO.getUserId());
            NettyAttrUtil.setAttrKeyUserId(channel, accountDTO.getUserId());
            M_DEVICE_ONLINE_USER_MAP.computeIfAbsent(accountDTO.getUserId(),
                    k -> new CopyOnWriteArrayList<>()).add(channel);
        }

        userDetailsDTO = BeanUtil.copyProperties(user, UserDetailsDTO.class);
        String token = tokenService.createToken(userDetailsDTO);
        userDetailsDTO.setToken(token);
        // 用户信息存入缓存
        redisService.hSet(LOGIN_USER, userDetailsDTO.getUserId(), JSONObject.parseObject(JSONObject.toJSONString(userDetailsDTO)), EXPIRE_USER);
        // 异步发送登录成功消息
        disruptorMQService.sendMsg(channel, R.ok(userDetailsDTO, LOGIN_SUCCESS));


    }
}
