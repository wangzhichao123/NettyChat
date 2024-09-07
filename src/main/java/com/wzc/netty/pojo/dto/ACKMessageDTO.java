package com.wzc.netty.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ACKMessageDTO {

    @ApiModelProperty(value = "消息ID", required = true, dataType = "String")
    private String messageId;

    @ApiModelProperty(value = "消息接收时间", required = false, dataType = "String")
    private LocalDateTime receiveTime;

}
