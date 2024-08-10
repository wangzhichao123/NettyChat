package com.wzc.netty.server;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.wzc.netty.service.TokenService;
import com.wzc.netty.util.NettyAttrUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;

import java.net.InetSocketAddress;
import java.util.Optional;

import static com.wzc.netty.constant.TokenConstant.*;

public class HttpHeaderHandler extends ChannelInboundHandlerAdapter {

    /**
     * 优先级必须高于自定义处理消息，因为握手采用 WebSocketServerProtocolHandler 处理。
     * 一旦协议升级、后续前端的部分操作不会再使用 HTTP 协议
     * @param ctx
     * @param o
     */

    private TokenService tokenService;

    private TokenService getTokenService() {
        return SpringUtil.getBean(TokenService.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object o) {
        this.tokenService = getTokenService();
//        String ipAddress = Ip2regionSearcher.getIpAddress(request);
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
            String ip = headers.get("X-Real-IP");
            if (StrUtil.isEmpty(ip)) {
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                ip = address.getAddress().getHostAddress();
            }
            NettyAttrUtil.setAttrKeyIP(ctx.channel(), ip);
            ctx.pipeline().remove(this);
            ctx.fireChannelRead(request);
        }else{
            ctx.fireChannelRead(o);
        }
    }
}