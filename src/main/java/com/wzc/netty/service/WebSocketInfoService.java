//package com.wzc.netty.service;
//
//import com.alibaba.fastjson2.JSONObject;
//import com.wzc.netty.constant.MessageCodeConstant;
//import com.wzc.netty.util.NettyAttrUtil;
//import io.netty.channel.Channel;
//import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
//import lombok.extern.slf4j.Slf4j;
//
//import java.util.Map;
//@Slf4j
//public class WebSocketInfoService {
//
//    /**
//     * 清除会话信息
//     * @param channel
//     */
//    public void clearSession(Channel channel) {
//        String userId = NettyAttrUtil.getUserId(channel);
//        // 清除会话信息
//        SessionHolder.channelGroup.remove(channel);
//        SessionHolder.channelMap.remove(userId);
//    }
//
//    /**
//     * 广播 ping 信息
//     */
//    public void sendPing() {
//        WsMessage webSocketMessage = new WsMessage();
//        webSocketMessage.setCode(MessageCodeConstant.PING_MESSAGE_CODE);
//        String message = JSONObject.toJSONString(webSocketMessage);
//        TextWebSocketFrame tws = new TextWebSocketFrame(message);
//        SessionHolder.channelGroup.writeAndFlush(tws);
//    }
//
//    /**
//     * 从缓存中移除Channel，并且关闭Channel
//     */
//    public void scanNotActiveChannel() {
//        Map<String, Channel> channelMap = SessionHolder.channelMap;
//        // 如果这个直播下已经没有连接中的用户会话了，删除频道
//        if (channelMap.size() == 0) {
//            return;
//        }
//        for (Channel channel : channelMap.values()) {
//            long lastHeartBeatTime = NettyAttrUtil.getLastHeartBeatTime(channel);
//            long intervalMillis = (System.currentTimeMillis() - lastHeartBeatTime);
//            if (!channel.isOpen()
//                    || !channel.isActive()
//                    || intervalMillis > 90000L) {
//                channelMap.remove(channel);
//                SessionHolder.channelGroup.remove(channel);
//                if (channel.isOpen() || channel.isActive()) {
//                    channel.close();
//                }
//            }
//        }
//    }
//
//}
//
