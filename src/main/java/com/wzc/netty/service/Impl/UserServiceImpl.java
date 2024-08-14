package com.wzc.netty.service.Impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzc.netty.exception.BizException;
import com.wzc.netty.mapper.UserMapper;
import com.wzc.netty.mapper.UserRelationshipMapper;
import com.wzc.netty.pojo.entity.User;
import com.wzc.netty.pojo.entity.UserRelationship;
import com.wzc.netty.pojo.vo.UserFriendsInfoVo;
import com.wzc.netty.pojo.vo.UserSearchInfoVo;
import com.wzc.netty.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.wzc.netty.enums.UserRelationshipStatusEnum.APPROVED;
import static com.wzc.netty.enums.UserRelationshipStatusEnum.PENDING;

/**
* @author wzc
* @description 针对表【user】的数据库操作Service实现
* @createDate 2024-07-11 23:15:06
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRelationshipMapper userRelationshipMapper;

    /**
     * 获取好友列表
     * @param userId
     * @return
     */
    @Override
    public List<UserFriendsInfoVo> getUserFriendListByUserId(String userId) {
        return userMapper.getUserFriendListByUserId(userId, APPROVED.getCode());
    }

    /**
     * 获取搜索用户信息
     * @param userId
     * @return
     */

    @Override
    public UserSearchInfoVo getSearchUserInfoByUserId(String userId) {
        return userMapper.getSearchUserInfoByUserId(userId);
    }

    /**
     * 添加用户
     * @param userFromId
     * @param userToId
     * @return
     */
    @Override
    public Boolean addUser(String userFromId, String userToId) {
        UserRelationship userFromRelationship = userRelationshipMapper.queryUserRelationship(userFromId, userToId, APPROVED.getCode());
        UserRelationship userToRelationship = userRelationshipMapper.queryUserRelationship(userToId, userFromId, APPROVED.getCode());
        if(ObjectUtil.isNotEmpty(userFromRelationship) || ObjectUtil.isNotEmpty(userToRelationship) ){
            throw new BizException("对方已是你的好友!");
        }
        User user = userMapper.queryUserByUserId(userToId);
        if(ObjectUtil.isEmpty(user)){
            throw new BizException("用户不存在!");
        }
        // 发起好友申请
        UserRelationship userRelationship = new UserRelationship();
        userRelationship.setUserFromId(userFromId);
        userRelationship.setUserToId(userToId);
        userRelationship.setStatus(PENDING.getCode());
        int result = userRelationshipMapper.insert(userRelationship);
        return BooleanUtil.isTrue(result > 0);
    }
}




