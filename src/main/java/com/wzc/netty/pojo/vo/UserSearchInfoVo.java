package com.wzc.netty.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "UserSearchInfoVo")
public class UserSearchInfoVo {
    @ApiModelProperty(name = "用户ID")
    private String userId;

    @ApiModelProperty(name = "用户昵称")
    private String nickname;

    @ApiModelProperty(name = "用户头像")
    private String userAvatar;
}
