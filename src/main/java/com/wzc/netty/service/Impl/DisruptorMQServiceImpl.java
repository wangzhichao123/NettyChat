package com.wzc.netty.service.Impl;

import cn.hutool.json.JSONUtil;
import com.lmax.disruptor.RingBuffer;
import com.wzc.netty.pojo.R;
import com.wzc.netty.pojo.dto.ChatMessageDTO;
import com.wzc.netty.pojo.dto.MessageModel;
import com.wzc.netty.service.DisruptorMQService;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.DelayQueue;

@Slf4j
@Service
public class DisruptorMQServiceImpl implements DisruptorMQService {

    @Resource
    private RingBuffer<MessageModel> messageModelRingBuffer;


    @Override
    public void sendMsg(Channel channel, R<?> msg) {
        log.info("Record the message: {}", JSONUtil.toJsonStr(msg));
        // 获取下一个Event槽的下标
        long sequence = messageModelRingBuffer.next();
        try {
            MessageModel event = messageModelRingBuffer.get(sequence);
            event.setChannel(channel);
            event.setMessage(JSONUtil.toJsonStr(msg));
            log.info("往消息队列中添加消息：{}", event);
        } catch (Exception e) {
            log.error("failed to add event to messageModelRingBuffer for : e = {},{}",e , e.getMessage());
        } finally {
            messageModelRingBuffer.publish(sequence);
        }
    }

}
