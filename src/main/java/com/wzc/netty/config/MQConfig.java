package com.wzc.netty.config;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.wzc.netty.handler.WSMessageEventFactory;
import com.wzc.netty.handler.WSMessageEventHandler;
import com.wzc.netty.pojo.dto.MessageModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@Slf4j
public class MQConfig {

    @Resource
    private ThreadPoolTaskExecutor websocketExecutor;

    /**
     *  指定 RingBuffer 字节大小，必须为2的N次方（能将求模运算转为位运算提高效率），否则将影响效率
     */
    private static final int bufferSize = 1024 * 256;

    @Bean
    public RingBuffer<MessageModel> messageModelRingBuffer() {
        WSMessageEventFactory factory = new WSMessageEventFactory();
        Disruptor<MessageModel> disruptor = new Disruptor<>(
                factory,
                bufferSize,
                websocketExecutor.getThreadPoolExecutor(),
                ProducerType.MULTI,
                new BlockingWaitStrategy());
        // 设置事件业务处理器---消费者
        disruptor.handleEventsWith(new WSMessageEventHandler());

        // 启动 Disruptor 线程
        disruptor.start();

        // 获取 RingBuffer 环形缓冲区，用于接取生产者生产的事件
        RingBuffer<MessageModel> ringBuffer = disruptor.getRingBuffer();

        return ringBuffer;
    }

}

