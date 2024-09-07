package com.wzc.netty.service.Impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.wzc.netty.enums.MessageTypeEnum;
import com.wzc.netty.enums.SendMessageTypeEnum;
import com.wzc.netty.exception.BizException;
import com.wzc.netty.mapper.MessageMapper;
import com.wzc.netty.mapper.UserMapper;
import com.wzc.netty.mapper.UserRelationshipMapper;
import com.wzc.netty.pojo.R;
import com.wzc.netty.pojo.dto.ACKMessageDTO;
import com.wzc.netty.pojo.dto.ChatMessageDTO;
import com.wzc.netty.pojo.dto.LoginDTO;
import com.wzc.netty.pojo.entity.Message;
import com.wzc.netty.pojo.entity.User;
import com.wzc.netty.pojo.entity.UserRelationship;
import com.wzc.netty.service.DisruptorMQService;
import com.wzc.netty.service.LoginService;
import com.wzc.netty.service.TokenService;
import com.wzc.netty.service.WebSocketService;
import com.wzc.netty.util.NettyAttrUtil;
import com.wzc.netty.util.RandomIDUtil;
import io.netty.channel.Channel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.wzc.netty.constant.CommonConstant.DISPLAY;
import static com.wzc.netty.constant.CommonConstant.DISPLAY_NOT;
import static com.wzc.netty.context.WebSocketChannelContext.*;
import static com.wzc.netty.enums.LoginStatusEnum.OFFLINE_STATUS;
import static com.wzc.netty.enums.MessageStatusEnum.*;
import static com.wzc.netty.enums.MessageStatusEnum.MESSAGE_SEND_PENDING;
import static com.wzc.netty.enums.MessageTypeEnum.*;
import static com.wzc.netty.enums.StatusCodeEnum.*;
import static com.wzc.netty.enums.StatusCodeEnum.MESSAGE_SEND_SUCCESS;
import static com.wzc.netty.enums.UserRelationshipStatusEnum.APPROVED;


@Service
public class WebSocketServiceImpl implements WebSocketService {

    private final LoginService loginService;
    private final TokenService tokenService;
    private final UserMapper userMapper;
    private final MessageMapper messageMapper;
    private final UserRelationshipMapper userRelationshipMapper;
    private final DisruptorMQService disruptorMQService;

    public WebSocketServiceImpl(LoginService loginService,
                                TokenService tokenService,
                                UserMapper userMapper,
                                MessageMapper messageMapper,
                                UserRelationshipMapper userRelationshipMapper,
                                DisruptorMQService disruptorMQService) {
        this.loginService = loginService;
        this.tokenService = tokenService;
        this.userMapper = userMapper;
        this.messageMapper = messageMapper;
        this.userRelationshipMapper = userRelationshipMapper;
        this.disruptorMQService = disruptorMQService;
    }


    // 获取指定用户和聊天室名称的聊天室
//    public static ChatRoom getChatRoom(Long userId, String chatRoomName) {
//        String key = userId + "_" + chatRoomName;
//        return chatRooms.get(key);
//    }

    /**
     * 登录成功, channel 就会存放 userId
     * 携带有效 Token, channel 就会存放 userId
     * @param channel
     */
    @Override
    public void addChannel(Channel channel) {
        // 连接终端 ChannelGroup 会自动移除连接无须手动移除
        GLOBAL_ONLINE_USER_GROUP.add(channel);
        FIRST_CHANNEL_MAP.put(channel, "");

    }

    @Override
    public void handleLoginReq(Channel channel, String data, String token) {
        if (StrUtil.isNotBlank(token)) {
            loginService.tokenLogin(channel, token);
        }else{
            loginService.login(channel, JSONUtil.toBean(data, LoginDTO.class));
        }
    }

    /**
     * 发送用户/群组消息
     * @param channel
     * @param data
     */
    @Override
    public void handleSendMessage(Channel channel, String data, String token) {
        // 1、校验数据
        if(StrUtil.isBlank(data)){
            disruptorMQService.sendMsg(channel, R.fail("用户状态异常"));
            clearSession(channel);
            return ;
        }
        String userId = tokenService.getSubject(token);
        String attrKeyUserId = NettyAttrUtil.getAttrKeyUserId(channel);
        // 2、校验用户 Token (解决同一个浏览器下多用户登录的问题)
        if(StrUtil.isBlank(userId) || !attrKeyUserId.equals(userId)){
            disruptorMQService.sendMsg(channel, R.fail("用户状态异常"));
            clearSession(channel);
            return ;
        }
        ChatMessageDTO messageDTO = JSONUtil.toBean(data, ChatMessageDTO.class);
        // 3、校验消息类型和发送消息类型
        MessageTypeEnum messageTypeEnum = MessageTypeEnum.of(messageDTO.getMessageType());
        SendMessageTypeEnum sendMessageTypeEnum = SendMessageTypeEnum.of(messageDTO.getSendMessageType());
        if(ObjectUtil.isEmpty(messageTypeEnum) || ObjectUtil.isEmpty(sendMessageTypeEnum)){
            disruptorMQService.sendMsg(channel, R.fail(INVALID_MESSAGE_TYPE));
            return ;
        }
        // 4、校验消息内容
        if(StrUtil.isBlank(messageDTO.getSendMessageContent())){
            disruptorMQService.sendMsg(channel, R.fail(MESSAGE_SEND_ERROR));
            return ;
        }
        // 5、根据消息类型判断私聊消息、群组消息
        switch (messageTypeEnum){
            case PRIVATE:
                privateMessageHandler(channel, messageDTO);
                break;
            case GROUP:
                groupMessageHandler(channel, messageDTO);
                break;
        }

    }

    private void privateMessageHandler(Channel channel, ChatMessageDTO messageDTO) {
        String userFromId = messageDTO.getUserFromId();
        String userToId = messageDTO.getUserToId();
        User sourceUser = userMapper.queryUserByUserId(userFromId);
        User targetUser = userMapper.queryUserByUserId(userToId);
        if(ObjectUtil.isEmpty(sourceUser) || ObjectUtil.isEmpty(targetUser)){
            disruptorMQService.sendMsg(channel, R.fail(USER_NOT_EXIST));
            return ;
        }
        List<Integer> validCodeList = List.of(APPROVED.getCode());
        UserRelationship fromRelationship = userRelationshipMapper.queryUserRelationship(userFromId, userToId, validCodeList);
        UserRelationship toRelationship = userRelationshipMapper.queryUserRelationship(userToId, userFromId, validCodeList);
        if(ObjectUtil.isEmpty(fromRelationship) && ObjectUtil.isEmpty(toRelationship)){
            disruptorMQService.sendMsg(channel, R.fail(NOT_FRIENDS));
            return ;
        }
        String messageId = messageDTO.getMessageId();
        if(StrUtil.isBlank(messageId)){
            disruptorMQService.sendMsg(channel, R.fail(MESSAGE_SEND_ERROR));
            return ;
        }
        messageDTO.setMessageId(messageId);
        Message message = new Message();
        message.setSendMessageStatus(MESSAGE_INIT.getCode());
        message.setReceiveMessageStatus(MESSAGE_INIT.getCode());
        BeanUtil.copyProperties(messageDTO, message);
        messageMapper.insert(message);
        // 发送给A用户进行发送确认
        ChatMessageDTO chatMessageDTO = messageContentHandler(messageDTO);
        chatMessageDTO.setDisplayStatus(DISPLAY_NOT);
        // 更新状态
        messageMapper.updateSendMessageStatus(messageId, MESSAGE_SEND_PENDING.getCode());
        disruptorMQService.sendMsg(channel, R.ok(chatMessageDTO, MESSAGE_SEND_ACK));
        // 在线处理
        CopyOnWriteArrayList<Channel> targetChannels = M_DEVICE_ONLINE_USER_MAP.get(targetUser.getUserId());
        if(ObjectUtil.isNotEmpty(targetChannels)){
            for (Channel targetChannel : targetChannels) {
                String targetUserId = targetChannel.attr(NettyAttrUtil.ATTR_KEY_USER_ID).get();
                if (targetUserId.equals(targetUser.getUserId())){
                    // 发送给B用户进行接收确认
                    messageMapper.updateReceiveMessageStatus(messageId, MESSAGE_SEND_PENDING.getCode());
                    disruptorMQService.sendMsg(targetChannel, R.ok(chatMessageDTO, MESSAGE_RECEIVE_ACK));
                }
            }
        }else {
            // 离线消息 (仅入库)
            messageMapper.updateSendMessageStatus(message.getMessageId(), MESSAGE_OFFLINE.getCode());
        }
    }

    private ChatMessageDTO messageContentHandler(ChatMessageDTO messageDTO) {
        // 处理消息：1：文本消息和表情包消息 2：图片消息 3：文件消息 4：语音消息 5：视频消息
        SendMessageTypeEnum sendMessageTypeEnum = SendMessageTypeEnum.of(messageDTO.getSendMessageType());
        String sendMessageContent = messageDTO.getSendMessageContent();
        switch (sendMessageTypeEnum){
            case TEXT:
                // 文本消息和表情包消息
                break;
            case IMAGE:
                // 图片消息
                break;
            case FILE:
                // 文件消息
                break;
            case AUDIO:
                // 语音消息
                break;
            case VIDEO:
                // 视频消息
                break;
        }
        return messageDTO;
    }

    /**
     * TODO
     * @param channel
     * @param messageDTO
     */
    private void groupMessageHandler(Channel channel, ChatMessageDTO messageDTO) {

    }

    /**
     * 检验 Token 是否生效
     * @param channel
     * @param token
     */

    @Override
    public void tokenLogin(Channel channel, String token) {
        loginService.tokenLogin(channel, token);
    }

    @Override
    public void handleHeartbeat(Channel channel) {
        // 更新连接的最后活跃时间
        NettyAttrUtil.setAttrKeyLastActiveTime(channel, String.valueOf(System.currentTimeMillis()));
        // channel.writeAndFlush(new TextWebSocketFrame("heartbeat-response"));
    }

    @Override
    public void clearSession(Channel channel) {
        // 1、全局删除
        FIRST_CHANNEL_MAP.remove(channel);
        // 2、信息删除
        String userId = NettyAttrUtil.getAttrKeyUserId(channel);
        if (StrUtil.isNotBlank(userId)) {
            CopyOnWriteArrayList<Channel> channels = M_DEVICE_ONLINE_USER_MAP.get(userId);
            for (Channel c : channels) {
                if (c == channel) {
                    channels.remove(channel);
                }
            }
            // 3、保存离线信息
            userMapper.saveUserLoginStatus(userId, OFFLINE_STATUS.getCode(), null, LocalDateTime.now());
            // 3、关闭 channel
            channel.close();
        }
    }

    /**
     * 处理ACK消息
     * @param channel
     * @param data
     */
    @Override
    public void handleACKMessage(Channel channel, String data, String token) {
        // 1、校验数据
        if(StrUtil.isBlank(data)){
            disruptorMQService.sendMsg(channel, R.fail(MESSAGE_ACK_ERROR));
            return ;
        }
        String userId = tokenService.getSubject(token);
        String attrKeyUserId = NettyAttrUtil.getAttrKeyUserId(channel);
        // 2、校验 Token
        if(StrUtil.isBlank(userId) && !userId.equals(attrKeyUserId)){
            disruptorMQService.sendMsg(channel, R.fail("用户状态异常"));
            clearSession(channel);
            return ;
        }
        List<String> messageIdList = JSONUtil.parseObj(data).getJSONArray("messageId").toList(String.class);
        if (ObjectUtil.isEmpty(messageIdList)){
            disruptorMQService.sendMsg(channel, R.fail(MESSAGE_ACK_ERROR));
            return;
        }
        for (String messageId : messageIdList){
            // 3、查询确认消息是否存在
            Message message = messageMapper.queryAckMessage(messageId);
            if(ObjectUtil.isEmpty(message)){
                this.disruptorMQService.sendMsg(channel, R.fail(MESSAGE_ACK_ERROR));
                return ;
            }
            ChatMessageDTO chatMessageDTO = BeanUtil.copyProperties(message, ChatMessageDTO.class);
            chatMessageDTO.setDisplayStatus(DISPLAY);
            String currentUserId = NettyAttrUtil.getAttrKeyUserId(channel);
            // 4、识别消息是发送方/接收方
            if (currentUserId.equals(message.getUserFromId())) {
                messageMapper.updateSendMessageStatus(chatMessageDTO.getMessageId(), MESSAGE_ACK_SUCCESS.getCode());
                disruptorMQService.sendMsg(channel, R.ok(chatMessageDTO, MESSAGE_SEND_SUCCESS));
            } else if (currentUserId.equals(message.getUserToId())) {
                messageMapper.updateReceiveMessageStatus(chatMessageDTO.getMessageId(), MESSAGE_ACK_SUCCESS.getCode());
                disruptorMQService.sendMsg(channel, R.ok(chatMessageDTO, MESSAGE_SEND_SUCCESS));
            }
        }
    }
}

