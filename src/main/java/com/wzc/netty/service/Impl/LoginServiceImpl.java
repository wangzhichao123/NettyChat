package com.wzc.netty.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.wzc.netty.context.LoginStrategyContext;
import com.wzc.netty.enums.LoginTypeEnum;
import com.wzc.netty.mapper.UserMapper;
import com.wzc.netty.pojo.R;
import com.wzc.netty.pojo.dto.LoginDTO;
import com.wzc.netty.pojo.dto.UserDetailsDTO;
import com.wzc.netty.pojo.entity.User;
import com.wzc.netty.service.DisruptorMQService;
import com.wzc.netty.service.LoginService;
import com.wzc.netty.service.TokenService;
import com.wzc.netty.util.NettyAttrUtil;
import io.netty.channel.Channel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.wzc.netty.context.WebSocketChannelContext.M_DEVICE_ONLINE_USER_MAP;
import static com.wzc.netty.enums.LoginStatusEnum.ONLINE_STATUS;
import static com.wzc.netty.enums.StatusCodeEnum.LOGIN_ERROR;
import static com.wzc.netty.enums.StatusCodeEnum.LOGIN_SUCCESS;

@Service
public class LoginServiceImpl implements LoginService {

    @Resource
    private LoginStrategyContext loginStrategyContext;
    @Resource
    private TokenService tokenService;
    @Resource
    private UserMapper userMapper;
    @Resource
    private DisruptorMQService disruptorMQService;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void login(Channel channel, LoginDTO loginDTO) {
        loginStrategyContext.executeLoginStrategy(channel, loginDTO.getLoginData(), LoginTypeEnum.of(loginDTO.getLoginType()));
    }

    @Override
    public void tokenLogin(Channel channel, String token) {
        boolean verifySuccess = tokenService.verifyToken(token);
        if (verifySuccess) {
            // 更新 channel
            String userId = tokenService.getSubject(token);
            NettyAttrUtil.setAttrKeyUserId(channel, userId);
            M_DEVICE_ONLINE_USER_MAP.computeIfAbsent(userId,
                            k -> new CopyOnWriteArrayList<>()).add(channel);
            // 登录状态入库
            LambdaQueryChainWrapper<User> wrapper = new LambdaQueryChainWrapper<>(userMapper);
            User user = wrapper.eq(User::getUserId, userId).one();
            user.setStatus(ONLINE_STATUS.getCode());
            user.setOnlineTime(LocalDateTime.now());
            userMapper.updateById(user);
            // 发送消息
            disruptorMQService.sendMsg(channel, R.ok(BeanUtil.copyProperties(user, UserDetailsDTO.class), LOGIN_SUCCESS));
        } else {
            disruptorMQService.sendMsg(channel, R.fail("token失效", LOGIN_ERROR));
        }
    }


}
