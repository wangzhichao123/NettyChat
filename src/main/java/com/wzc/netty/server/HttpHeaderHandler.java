package com.wzc.netty.server;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.wzc.netty.pojo.IpInfo;
import com.wzc.netty.service.TokenService;
import com.wzc.netty.util.IpUtil;
import com.wzc.netty.util.NettyAttrUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import static com.wzc.netty.constant.TokenConstant.TOKEN_HEADER;
import static com.wzc.netty.constant.TokenConstant.TOKEN_PREFIX;

@Slf4j
public class HttpHeaderHandler extends ChannelInboundHandlerAdapter {


    private TokenService getTokenService() {
        return SpringUtil.getBean(TokenService.class);
    }

    private TokenService tokenService;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("————HttpHeaderHandler已装载————");
        this.tokenService = getTokenService();
    }

    /**
     * 优先级必须高于自定义处理消息，因为握手采用 WebSocketServerProtocolHandler 处理。
     * 一旦协议升级、后续前端的部分操作不会再使用 HTTP 协议
     * @param ctx
     * @param o
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object o) {
        if (o instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) o;
            HttpHeaders headers = request.headers();
            String token = Optional.ofNullable(headers.get(TOKEN_HEADER)).orElse("").replace(TOKEN_PREFIX, "");
            if (StrUtil.isNotBlank(token)) {
                String userId = tokenService.getSubject(token);
                if (StrUtil.isNotBlank(userId)) {
                    NettyAttrUtil.setAttrKeyUserId(ctx.channel(), userId);
                    NettyAttrUtil.setAttrKeyToken(ctx.channel(), token);
                }
            }
            String ip = IpUtil.getIpAddress(request, ctx);
            IpInfo ipInfo = IpUtil.parse(ip);
            NettyAttrUtil.setAttrKeyIP(ctx.channel(), ip);
//            ctx.pipeline().remove(this);
            ctx.fireChannelRead(request);
        }else{
            ctx.fireChannelRead(o);
        }
    }
}