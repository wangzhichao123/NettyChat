package com.wzc.netty.service.Impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
    public List<UserFriendsInfoVo> getUserFriendListByUserId(String userId) {
        return userMapper.getUserFriendListByUserId(userId, APPROVED.getCode());
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
        List<Integer> validCodeList = List.of(PENDING.getCode(), APPROVED.getCode());
        UserRelationship userFromRelationship = userRelationshipMapper.queryUserRelationship(userFromId, userToId, validCodeList);
        UserRelationship userToRelationship = userRelationshipMapper.queryUserRelationship(userToId, userFromId, validCodeList);
        if(ObjectUtil.isNotEmpty(userFromRelationship) && ObjectUtil.isNotEmpty(userToRelationship)) {
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
    public Boolean approveOrRejectUser(String userFromId, String userToId, Boolean flag) {
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
            if(flag){
                userRelationship.setStatus(APPROVED.getCode());
                record = userRelationshipMapper.updateById(userRelationship);
            }else{
                userRelationship.setStatus(REJECTED.getCode());
                record = userRelationshipMapper.updateById(userRelationship);
            }

        }
        return BooleanUtil.isTrue(record > 0);
    }

}




