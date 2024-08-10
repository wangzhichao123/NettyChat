package com.wzc.netty;

import com.wzc.netty.server.NettyWebSocketServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@SpringBootApplication
@MapperScan("com.wzc.netty.mapper")
public class NettyDemoApplication {
    /**
     * 解决 Netty 监听端口覆盖 SpringBoot 启动端口的问题
     */
    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(NettyDemoApplication.class, args);
        ExecutorService service = Executors.newFixedThreadPool(1);
        service.execute(ctx.getBean(NettyWebSocketServer.class));
        service.shutdown();
    }


}
