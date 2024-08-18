package com.wzc.netty.handler;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.lmax.disruptor.EventHandler;
import com.wzc.netty.pojo.dto.ChatMessageDTO;
import com.wzc.netty.pojo.dto.MessageModel;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WSMessageEventHandler implements EventHandler<MessageModel> {


    @Override
    public void onEvent(MessageModel event, long sequence, boolean endOfBatch) {
        try {
            if(event != null){
                log.info("消费者处理消息开始");
                Channel channel = event.getChannel();
                String message = event.getMessage();
                ChatMessageDTO chatMessageDTO = JSONUtil.toBean(message, ChatMessageDTO.class);

                channel.writeAndFlush(new TextWebSocketFrame(event.getMessage()));

                log.info("消费者消费的信息是：{}", event.getMessage());
            }
        } catch (Exception e) {
            log.info("消费者处理消息失败");
        }
        log.info("消费者处理消息结束");
    }

}
