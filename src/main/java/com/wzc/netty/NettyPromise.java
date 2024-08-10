package com.wzc.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;

import java.util.concurrent.ExecutionException;

public class NettyPromise {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        /**
         * 堆内存：分配效率高，读写效率低
         * 直接内存：分配效率低，读写效率高
         * 直接内存对 GC 压力小，因为这部分内存不受 JVM 垃圾回收的管理。需要注意，直接内存分配需要手动释放，否则会造成内存泄漏。
         * 堆内存对 GC 压力大，因为这部分内存需要 JVM 垃圾回收，如果堆内存中存在大量对象，那么 JVM 垃圾回收就会非常耗时。
         */
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        ByteBuf heapBuffer = ByteBufAllocator.DEFAULT.heapBuffer(10);
        ByteBuf directBuffer = ByteBufAllocator.DEFAULT.directBuffer(10);
        System.out.println(buffer);
        EventLoop eventLoop = new NioEventLoopGroup().next();
        // 2、结果容器
        DefaultPromise<Integer> promise = new DefaultPromise<>(eventLoop);
        new Thread(() -> {
            System.out.println("开始计算");
            try{
                // 3、设置结果(可以是正确结果/也可以是错误异常)
                promise.setSuccess(100);
                Thread.sleep(1000L);
            }catch (InterruptedException e){
                e.printStackTrace();
                promise.setFailure(e);
            }

        }).start();
        // 4、接收结果的线程
        System.out.println("等待结果...");
        System.out.println("接收结果: " + promise.get());
    }
}
