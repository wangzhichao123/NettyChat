package com.wzc.netty.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ACKMessageDTO {

    @ApiModelProperty(value = "消息ID", required = true, dataType = "String")
    private String messageId;
}
