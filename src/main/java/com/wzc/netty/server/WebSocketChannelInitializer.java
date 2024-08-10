package com.wzc.netty.server;

import com.wzc.netty.exception.ExceptionLoggingHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;


public class WebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        // 异常日志
        socketChannel.pipeline().addLast(new ExceptionLoggingHandler());
        // 60秒客户端没有向服务器发送心跳则关闭连接
        socketChannel.pipeline().addLast(new IdleStateHandler(60, 0, 0));
        // HTTP请求编码器、解码器
        socketChannel.pipeline().addLast(new HttpServerCodec());
        // 这个处理器用于将多个 HTTP 消息合并成一个 FullHttpRequest 对象
        socketChannel.pipeline().addLast(new HttpObjectAggregator(65536));
        // 这个处理器用于支持异步发送大的数据流、比如:文件下载等
        socketChannel.pipeline().addLast(new ChunkedWriteHandler());
        // 处理请求头 (注：一旦握手完成、协议升级为 WebSocket 协议、就获取不到 HTTP 请求头了。只有前端页面刷新，才会再次触发)
        socketChannel.pipeline().addLast(new HttpHeaderHandler());
        // 提供握手处理、数据帧处理、状态管理、升级检测
        socketChannel.pipeline().addLast(new WebSocketServerProtocolHandler("/ws"));
        // 处理 websocket 和处理消息的发送
        socketChannel.pipeline().addLast(new WebSocketSimpleChannelInboundHandler());
    }
}
