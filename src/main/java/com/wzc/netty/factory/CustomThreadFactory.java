package com.wzc.netty.factory;

import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

public class CustomThreadFactory extends CustomizableThreadFactory {
    private static final String THREAD_NAME_PREFIX = "websocket-executor-";
    public CustomThreadFactory() {
        super(THREAD_NAME_PREFIX);
    }
}