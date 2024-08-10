package com.wzc.netty.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzc.netty.exception.BizException;
import com.wzc.netty.mapper.GroupMapper;
import com.wzc.netty.pojo.ChatRoom;
import com.wzc.netty.pojo.entity.Group;
import com.wzc.netty.service.GroupService;
import com.wzc.netty.util.RandomIDUtil;
import com.wzc.netty.context.WebSocketChannelContext;
import org.springframework.stereotype.Service;


/**
* @author wzc
* @description 针对表【group】的数据库操作Service实现
* @createDate 2024-07-11 23:15:06
*/
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, Group>
    implements GroupService {

    /**
     * 创建聊天室
     * @param chatRoomName
     */
    @Override
    public void createChatRoom(String chatRoomName) {
        Group group = getGroupByName(chatRoomName);
        if (group != null) {
            throw new BizException("聊天室名字 [" + chatRoomName + "] 已经存在!");
        }

        String groupId;
        do {
            groupId = RandomIDUtil.generateRandomID();
        } while (getGroupById(groupId) != null);

        group = Group.builder()
                .id(Long.parseLong(groupId))
                .groupName(chatRoomName)
                .build();

        this.save(group);

        WebSocketChannelContext.CHAT_ROOMS.put(chatRoomName, new ChatRoom());

    }

    private Group getGroupByName(String chatRoomName) {
        return new LambdaQueryChainWrapper<>(this.baseMapper)
                .eq(Group::getGroupName, chatRoomName)
                .one();
    }

    private Group getGroupById(String groupId) {
        return new LambdaQueryChainWrapper<>(this.baseMapper)
                .eq(Group::getId, groupId)
                .one();
    }
}




