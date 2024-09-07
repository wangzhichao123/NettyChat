package com.wzc.netty.handler;

import com.lmax.disruptor.EventHandler;
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
                channel.writeAndFlush(new TextWebSocketFrame(message));
                log.info("消费者消费的信息是：{}", event.getMessage());
            }
        } catch (Exception e) {
            log.info("消费者处理消息失败");
        }
        log.info("消费者处理消息结束");
    }

}
