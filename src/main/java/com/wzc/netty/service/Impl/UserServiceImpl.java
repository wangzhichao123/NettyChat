package com.wzc.netty.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzc.netty.mapper.UserMapper;
import com.wzc.netty.pojo.entity.User;
import com.wzc.netty.pojo.vo.UserFriendsInfoVo;
import com.wzc.netty.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.wzc.netty.enums.UserRelationshipStatusEnum.APPROVED;

/**
* @author wzc
* @description 针对表【user】的数据库操作Service实现
* @createDate 2024-07-11 23:15:06
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    @Resource
    private UserMapper userMapper;

    /**
     * 获取好友列表
     * @param userId
     * @return
     */
    @Override
    public List<UserFriendsInfoVo> getFriendsByUserId(String userId) {
        return userMapper.getFriendsByUserId(userId, APPROVED.getCode());
    }
}




