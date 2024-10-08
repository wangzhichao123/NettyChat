package com.wzc.netty.service.Impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzc.netty.context.PaginationContext;
import com.wzc.netty.exception.BizException;
import com.wzc.netty.mapper.MessageMapper;
import com.wzc.netty.pojo.dto.ChatMessageDTO;
import com.wzc.netty.pojo.entity.Message;
import com.wzc.netty.pojo.vo.UserFriendsInfoVo;
import com.wzc.netty.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Executors;

import static com.wzc.netty.enums.UserRelationshipStatusEnum.APPROVED;

/**
* @author wzc
* @description 针对表【message】的数据库操作Service实现
* @createDate 2024-07-11 23:15:06
*/
@Slf4j
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
    implements MessageService {

    @Resource
    private MessageMapper messageMapper;
    @Override
    public Page<ChatMessageDTO> getUserFriendMessage(String userFromId, String userToId) {
        if(StrUtil.isBlank(userFromId) || StrUtil.isBlank(userToId)){
            throw new BizException("用户ID不能为空!");
        }
        Page<UserFriendsInfoVo> page = new Page<>(PaginationContext.getCurrent(), PaginationContext.getSize());
        return messageMapper.getUserFriendMessage(page, userFromId, userToId);
    }

    @Override
    public Page<ChatMessageDTO> getUserGroupMessage(String userFromId, String groupId) {
        return null;
    }

//    private final static ConcurrentHashMap<String, DelayQueue<ChatMessageDTO>> delayMap = new ConcurrentHashMap<>();
//
//    @Override
//    public void addDelayMessage(ChatMessageDTO delayMessage) {
//        delayMap.computeIfAbsent(delayMessage.getUserToId(), k -> new DelayQueue<>()).add(delayMessage);
//    }

//    public void updateStatus(ChatMessageDTO chatMessageDTO) {
//        //数据库改变订单状态
//        orderService.cancelOrder(orderNo);
//        this.up;
//        Iterator<ChatMessageDTO> iterator = delayQueue.iterator();
//    }

//    public void removeDelayMessage(ChatMessageDTO chatMessageDTO) {
//        DelayQueue<ChatMessageDTO> chatMessageDTOS = delayMap.get(chatMessageDTO.getUserToId());
//        if (chatMessageDTOS != null) {
//            Iterator<ChatMessageDTO> iterator = chatMessageDTOS.iterator();
//            while (iterator.hasNext()) {
//                ChatMessageDTO next = iterator.next();
//                if (next.getMessageId().equals(chatMessageDTO.getMessageId())) {
//                    iterator.remove();
//                    break;
//                }
//            }
//        }
//    }

//    @PostConstruct
//    public void initOrderStatus() {
//        //启动一个线程持续监听订单超时
//        Executors.newSingleThreadExecutor().execute(() -> {
//            try {
//                while (true) {
//                    if (!delayQueue.isEmpty()) {
//                        // 超时的消息会被取出
//                        ChatMessageDTO chatMessageDTO = delayQueue.take();
//                        // 修改状态并移除
//                        updateStatus(chatMessageDTO);
//                    }
//                }
//            } catch (InterruptedException e) {
//                log.error("InterruptedException error:", e);
//            }
//        });
//    }
}




