package com.wzc.netty.pojo.dto;

import io.netty.channel.Channel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 消息体
 */
@Data
public class MessageModel {

    @ApiModelProperty("消息内容")
    private String message;

    private Channel channel;

}
