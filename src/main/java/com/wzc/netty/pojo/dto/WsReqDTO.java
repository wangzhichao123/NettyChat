package com.wzc.netty.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


import javax.validation.constraints.NotBlank;

@Data
@ApiModel(description = "鉴权")
public class WsReqDTO {

    @ApiModelProperty(name = "cmd", value = "命令码", required = true, dataType = "Integer")
    private Integer cmd;

    @ApiModelProperty(name = "data", value = "请求体", required = true, dataType = "Object")
    private String data;

    @ApiModelProperty(name = "token", value = "token", required = true, dataType = "String")
    private String token;
}
