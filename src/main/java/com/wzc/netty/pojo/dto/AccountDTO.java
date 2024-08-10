package com.wzc.netty.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(description = "AccountDTO")
public class AccountDTO {

    @ApiModelProperty("用户名")
    private String userId;

    @ApiModelProperty("密码")
    private String password;


}
