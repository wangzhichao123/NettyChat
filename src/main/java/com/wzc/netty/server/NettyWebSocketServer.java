package com.wzc.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.NettyRuntime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.wzc.netty.constant.WebSocketConstant.WEB_SOCKET_PORT;

@Slf4j
@Component
public class NettyWebSocketServer implements Runnable{

    @Override
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(NettyRuntime.availableProcessors());
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.childHandler(new WebSocketChannelInitializer());
            log.info("################服务器创建成功################");
            Channel channel = serverBootstrap.bind(WEB_SOCKET_PORT).sync().channel();
            // 阻塞创建 Netty Server 的线程 (这里指的是线程池中那个线程)
            channel.closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
            log.info("################服务器创建失败################");
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
