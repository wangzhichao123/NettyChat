package com.wzc.netty.context;

import com.wzc.netty.pojo.entity.Message;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;

@Component
public class MessageContext {
//    private ConcurrentHashMap<String, Message> messageMap = new ConcurrentHashMap<>();
//    private DelayQueue<Message> delayQueue = new DelayQueue<>();
//
//    public void addDelayMessage(Message message) {
//        messageMap.put(message.getMessageId(), message);
//        delayQueue.offer(message);
//    }
//
//    public void onMessageReceived(String messageId) {
//        Message message = messageMap.remove(messageId);
//        if (message != null) {
//            delayQueue.remove(message);  // 从延时队列中移除
//            long currentTime = System.currentTimeMillis();
//            if (currentTime > message.getTimestamp()) {
//                System.out.println("Message confirmation received, but it was too late.");
//            } else {
//                System.out.println("Message confirmation received in time.");
//            }
//        } else {
//            System.out.println("Message confirmation received, but message not found.");
//        }
//    }
//
//    public void processDelayedMessages() {
//        while (true) {
//            try {
//                Message message = delayQueue.take();  // 获取并移除队列中的超时消息
//                if (message != null) {
//                    messageMap.remove(message.getMessageId());  // 从Map中移除超时消息
//                    System.out.println("Message with ID: " + message.getMessageId() + " has timed out.");
//                }
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                break;
//            }
//        }
//    }
}

