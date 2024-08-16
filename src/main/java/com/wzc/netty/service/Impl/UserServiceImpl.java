package com.wzc.netty.service.Impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzc.netty.context.PaginationContext;
import com.wzc.netty.exception.BizException;
import com.wzc.netty.mapper.UserMapper;
import com.wzc.netty.mapper.UserRelationshipMapper;
import com.wzc.netty.pojo.R;
import com.wzc.netty.pojo.entity.User;
import com.wzc.netty.pojo.entity.UserRelationship;
import com.wzc.netty.pojo.vo.UserFriendsInfoVo;
import com.wzc.netty.pojo.vo.UserSearchInfoVo;
import com.wzc.netty.service.DisruptorMQService;
import com.wzc.netty.service.UserService;
import com.wzc.netty.util.NettyAttrUtil;
import io.netty.channel.Channel;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.wzc.netty.constant.CommonConstant.APPROVE_CODE;
import static com.wzc.netty.constant.CommonConstant.REJECT_CODE;
import static com.wzc.netty.context.WebSocketChannelContext.M_DEVICE_ONLINE_USER_MAP;
import static com.wzc.netty.enums.StatusCodeEnum.FRIEND_APPLICATION;
import static com.wzc.netty.enums.UserRelationshipStatusEnum.*;


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

    @Resource
    private DisruptorMQService disruptorMQService;

    /**
     * 获取好友列表
     * @param userId
     * @return
     */
    @Override
    public Page<UserFriendsInfoVo> getUserFriendListByUserId(String userId) {
        Page<UserFriendsInfoVo> page = new Page<>(PaginationContext.getCurrent(), PaginationContext.getSize());
        return userMapper.getUserFriendListByUserId(page, userId, APPROVED.getCode());
    }

    /**
     * 获取搜索好友信息
     * @param userId
     * @return
     */

    @Override
    public UserSearchInfoVo getSearchUserInfoByUserId(String userId) {
        return userMapper.getSearchUserInfoByUserId(userId);
    }

    /**
     * 添加好友
     * @param userFromId
     * @param userToId
     * @return
     */
    @Override
    public Boolean addUser(String userFromId, String userToId) {
        if(StrUtil.isBlank(userFromId) || StrUtil.isBlank(userToId)){
            throw new BizException("用户ID不能为空!");
        }
        List<Integer> validCodeList = List.of(PENDING.getCode(), APPROVED.getCode());
        UserRelationship userFromRelationship = userRelationshipMapper.queryUserRelationship(userFromId, userToId, validCodeList);
        UserRelationship userToRelationship = userRelationshipMapper.queryUserRelationship(userToId, userFromId, validCodeList);
        if(ObjectUtil.isNotEmpty(userFromRelationship) || ObjectUtil.isNotEmpty(userToRelationship)) {
            throw new BizException("请勿重复添加!");
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
        userRelationship.setAddTime(LocalDateTime.now());
        int result = userRelationshipMapper.insert(userRelationship);
        CopyOnWriteArrayList<Channel> targetChannels = M_DEVICE_ONLINE_USER_MAP.get(userToId);
        if(ObjectUtil.isNotEmpty(targetChannels)){
            for (Channel targetChannel : targetChannels) {
                String targetUserId = targetChannel.attr(NettyAttrUtil.ATTR_KEY_USER_ID).get();
                if (targetUserId.equals(userToId)){
                    disruptorMQService.sendMsg(targetChannel, R.ok(userRelationship, FRIEND_APPLICATION));
                }
            }
        }
        return BooleanUtil.isTrue(result > 0);
    }

    /**
     * 通过好友申请
     * @param userFromId
     * @param userToId
     * @return
     */
    @Override
    public Boolean approveOrRejectUser(String userFromId, String userToId, Integer code) {
        if(StrUtil.isBlank(userFromId) || StrUtil.isBlank(userToId)){
            throw new BizException("用户ID不能为空!");
        }
        int record = 0;
        List<Integer> validCodeList = List.of(PENDING.getCode());
        UserRelationship userRelationship = userRelationshipMapper.queryUserRelationship(userFromId, userToId, validCodeList);
        if(ObjectUtil.isEmpty(userRelationship)){
            throw new BizException("暂无添加好友请求！");
        }
        Integer userRelationshipStatus = userRelationship.getStatus();
        if(userRelationshipStatus == APPROVED.getCode()){
            throw new BizException("对方已是你的好友！");
        }
        if(userRelationshipStatus == REJECTED.getCode()){
            throw new BizException("对方已拒绝！");
        }
        if(userRelationshipStatus == PENDING.getCode()){
            if(code == APPROVE_CODE){
                userRelationship.setStatus(APPROVED.getCode());
            }else if(code == REJECT_CODE){
                userRelationship.setStatus(REJECTED.getCode());
            }
            record = userRelationshipMapper.updateById(userRelationship);
        }
        return BooleanUtil.isTrue(record > 0);
    }

    /**
     * 获取好友申请列表
     * @param userId
     * @return Page<UserFriendsInfoVo>
     */
    @Override
    public Page<UserFriendsInfoVo> getUserApplicationList(String userId) {
        if(StrUtil.isBlank(userId)){
            throw new BizException("用户ID不能为空!");
        }
        Page<UserFriendsInfoVo> page = new Page<>(PaginationContext.getCurrent(), PaginationContext.getSize());
        return userMapper.getUserApplicationList(page, userId, PENDING.getCode());
    }


    /**
     * 获取游标好友申请列表
     * @param userId
     * @param id
     * @return Page<UserFriendsInfoVo>
     */
    @Override
    public Page<UserFriendsInfoVo> getCursorUserApplicationList(String userId, Long id) {
        if(ObjectUtil.isNull(id) || StrUtil.isBlank(userId)){
            throw new BizException("ID不能为空!");
        }
        Page<UserFriendsInfoVo> page = new Page<>(PaginationContext.getCurrent(), PaginationContext.getSize());
        return userMapper.getCursorUserApplicationList(page, userId, id, PENDING.getCode());
    }

    @Override
    public Page<UserFriendsInfoVo> getCursorUserFriendList(String userId, Long id) {
        if(ObjectUtil.isNull(id) || StrUtil.isBlank(userId)){
            throw new BizException("ID不能为空!");
        }
        Page<UserFriendsInfoVo> page = new Page<>(PaginationContext.getCurrent(), PaginationContext.getSize());
        return userMapper.getCursorUserApplicationList(page, userId, id, APPROVED.getCode());
    }

}




