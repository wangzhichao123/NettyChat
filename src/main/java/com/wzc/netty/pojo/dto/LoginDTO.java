package com.wzc.netty.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@ApiModel(description = "登录DTO")
public class LoginDTO {


    @ApiModelProperty(name = "loginType", value = "登录类型", required = true, dataType = "Integer")
    private Integer loginType;


    @ApiModelProperty(name = "loginData", value = "登录请求信息", required = true, dataType = "String")
    private String loginData;


}
