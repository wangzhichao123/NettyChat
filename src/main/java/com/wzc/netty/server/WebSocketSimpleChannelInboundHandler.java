package com.wzc.netty.server;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.wzc.netty.enums.CommandTypeEnum;
import com.wzc.netty.exception.BizException;
import com.wzc.netty.pojo.dto.WsReqDTO;
import com.wzc.netty.service.WebSocketService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import com.wzc.netty.util.NettyAttrUtil;


/**
 * Netty ChannelHandler，用来处理客户端和服务端的会话生命周期事件（握手、建立连接、断开连接、收消息等）
 * @Author 
 * @Description 接收请求，接收 WebSocket 信息的控制类
 */
@Slf4j
public class WebSocketSimpleChannelInboundHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    /**
     *  handler的生命周期回调接口调用顺序:
     *  handlerAdded -> channelRegistered -> channelActive
     *  -> channelRead -> channelReadComplete
     *  -> channelInactive -> channelUnRegistered -> handlerRemoved
     *
     * handlerAdded: 新建立的连接会按照初始化策略，把handler添加到该channel的pipeline里面，也就是channel.pipeline.addLast(new LifeCycleInBoundHandler)执行完成后的回调；
     * channelRegistered: 当该连接分配到具体的worker线程后，该回调会被调用。
     * channelActive：channel的准备工作已经完成，所有的pipeline添加完成，并分配到具体的线上上，说明该channel准备就绪，可以使用了。
     * channelRead：客户端向服务端发来数据，每次都会回调此方法，表示有数据可读；
     * channelReadComplete：服务端每次读完一次完整的数据之后，回调该方法，表示数据读取完毕；
     * channelInactive：当连接断开时，该回调会被调用，说明这时候底层的TCP连接已经被断开了。
     * channelUnRegistered: 对应channelRegistered，当连接关闭后，释放绑定的workder线程；
     * handlerRemoved： 对应handlerAdded，将handler从该channel的pipeline移除后的回调方法。
     */
    private WebSocketService webSocketService;

    /**
     * 这个方法会在客户端与服务端建立 WebSocket 连接时被调用。
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //创建新的 WebSocket 连接，保存当前 channel
        log.info("————客户端与服务端连接开启————");
    }

    /**
     * 这个方法会在客户端与服务端的 WebSocket 连接断开时被调用
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("————客户端与服务端连接断开————");
        log.info("触发 channelInactive 掉线![{}]", ctx.channel().id());
        this.webSocketService.clearSession(ctx.channel());
    }

//    /**
//     * 这个方法会在服务端接收完客户端发送的数据之后被调用
//     */
//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        // 问题1：服务端接收完客户端发送的数据，推送到了哪里？
//        ctx.flush();
//    }

    /**
     * 这个方法会在 WebSocketServerProtocolHandler 被添加到 ChannelPipeline 中时被调用。
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("————WebSocketSimpleChannelInboundHandler已装载————");
        this.webSocketService = getService();
    }

    /**
     * 这个方法会在 WebSocketServerProtocolHandler 从 ChannelPipeline 中被移除时被调用。
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("————WebSocketSimpleChannelInboundHandler已移除————");
    }

    /**
     * 当 WebSocket 连接发生异常时被调用
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("异常:", cause);
        ctx.close();
    }

    /**
     * 服务端处理客户端websocket请求的核心方法
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame) throws Exception {
        WsReqDTO wsReqDTO = JSONUtil.toBean(frame.text(), WsReqDTO.class);
        CommandTypeEnum commandTypeEnum = CommandTypeEnum.of(wsReqDTO.getCmd());
        switch (commandTypeEnum) {
            case LOGIN_MESSAGE:
                this.webSocketService.handleLoginReq(ctx.channel(), wsReqDTO.getData(), wsReqDTO.getToken());
                break;
            case SEND_MESSAGE:
                this.webSocketService.handleSendMessage(ctx.channel(), wsReqDTO.getData(), wsReqDTO.getToken());
                break;
            case ACK_MESSAGE:
                this.webSocketService.handleACKMessage(ctx.channel(), wsReqDTO.getData());
            case HEARTBEAT_MESSAGE:
                this.webSocketService.handleHeartbeat(ctx.channel());
                break;
            default:
                throw new BizException("Unsupported command code: " + wsReqDTO.getCmd());
        }
    }

    /**
     * 检查空闲状态
     * 1）读空闲：即多长时间没有接受到客户端发送数据
     * 2）写空闲：即多长时间没有向客户端发送数据
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 握手事件处理
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            this.webSocketService.addChannel(ctx.channel());
            String token = NettyAttrUtil.getAttrKeyToken(ctx.channel());
            // 前端页面刷新会触发
            if (StrUtil.isNotBlank(token)) {
                this.webSocketService.tokenLogin(ctx.channel(), token);
            }
        }
        // 读空闲事件处理
        else if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                this.webSocketService.clearSession(ctx.channel());
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    /**
     * 处理客户端向服务端发起 http 握手请求的业务
     * WebSocket在建立握手时，数据是通过HTTP传输的。但是建立之后，在真正传输时候是不需要HTTP协议的。
     *
     * WebSocket 连接过程：
     * 首先，客户端发起http请求，经过3次握手后，建立起TCP连接；http请求里存放WebSocket支持的版本号等信息，如：Upgrade、Connection、WebSocket-Version等；
     * 然后，服务器收到客户端的握手请求后，同样采用HTTP协议回馈数据；
     * 最后，客户端收到连接成功的消息后，开始借助于TCP传输信道进行全双工通信。
     */


    /**
     * 服务端向客户端响应消息
     */
    private void sendHttpResponse(ChannelHandlerContext ctx, DefaultFullHttpResponse response) {
        if (response.status().code() != 200) {
            //创建源缓冲区
            ByteBuf byteBuf = Unpooled.copiedBuffer(response.status().toString(), CharsetUtil.UTF_8);
            //将源缓冲区的数据传送到此缓冲区
            response.content().writeBytes(byteBuf);
            //释放源缓冲区
            byteBuf.release();
        }
        //写入请求，服务端向客户端发送数据
        ChannelFuture channelFuture = ctx.channel().writeAndFlush(response);
        if (response.status().code() != 200) {
        	/**
        	 * 如果请求失败，关闭 ChannelFuture
        	 * ChannelFutureListener.CLOSE 源码：future.channel().close();
        	 */
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private WebSocketService getService() {
        return SpringUtil.getBean(WebSocketService.class);
    }


}
